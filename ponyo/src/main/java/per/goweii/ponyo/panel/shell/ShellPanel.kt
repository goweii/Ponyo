package per.goweii.ponyo.panel.shell

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel
import per.goweii.ponyo.shell.AppShell
import java.lang.StringBuilder

class ShellPanel : Panel() {
    private var et_shell_input: EditText? = null
    private var rv_shell_output: RecyclerView? = null

    private var adapter: ShellAdapter? = null

    override fun getPanelName(): String = "命令行"

    override fun getLayoutRes(): Int = R.layout.ponyo_panel_shell

    override fun onCreated(view: View) {
        super.onCreated(view)
        rv_shell_output = view.findViewById(R.id.rv_shell_output)
        et_shell_input = view.findViewById(R.id.et_shell_input)
        rv_shell_output?.layoutManager = LinearLayoutManager(context)
        adapter = ShellAdapter()
        rv_shell_output?.adapter = adapter
        et_shell_input?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleCmd(v.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        scope?.launch(Dispatchers.IO) { AppShell.forkShellProcess(context) }
        AppShell.onConnected = this::handleConnected
        AppShell.onDisconnect = this::handleDisconnect
        AppShell.onResult = this::handleResult
    }

    @SuppressLint("SetTextI18n")
    override fun onVisible(view: View) {
        super.onVisible(view)
        scope?.launch {
            val success = withContext(Dispatchers.IO) {
                AppShell.connectShellProcess()
            }
            if (success) {
                et_shell_input?.requestFocus()
            } else {
                handleConnectFailed()
            }
        }
    }

    private fun handleConnectFailed() {
        addOutput("Connect failed!")
        scrollToBottom()
    }

    private fun handleConnected() {
        addOutput("Connect success!")
        scrollToBottom()
    }

    private fun handleDisconnect() {
        addOutput("Disconnected!")
        scrollToBottom()
    }

    private fun handleCmd(cmd: String) {
        scope?.launch {
            val newCmd = preProcessCmd(cmd)
            if (newCmd.isNullOrEmpty()) {
                et_shell_input?.setText("")
                return@launch
            }
            val success = withContext(Dispatchers.IO) {
                AppShell.exec(newCmd)
            }
            if (success) {
                et_shell_input?.setText("")
                addInput(":/ $ $newCmd")
                scrollToBottom()
            }
        }
    }

    private fun handleResult(result: String) {
        scope?.launch {
            addOutput(result)
            scrollToBottom()
        }
    }

    private fun addInput(text: String) {
        adapter?.addInput(ShellInputEntity(text))
    }

    private fun addOutput(text: String) {
        val adapter = adapter ?: return
        val datas = adapter.datas
        if (datas.isEmpty()) {
            adapter.addOutput(ShellOutputEntity(StringBuilder(text)))
        } else {
            val last = datas.last()
            if (last is ShellOutputEntity) {
                last.append(text)
                adapter.notifyItemChanged(datas.lastIndex)
            } else {
                adapter.addOutput(ShellOutputEntity(StringBuilder(text)))
            }
        }
    }

    private fun scrollToBottom() {
        val count = adapter?.itemCount ?: 0
        if (count > 0) {
            rv_shell_output?.scrollToPosition(count - 1)
        }
    }

    private fun preProcessCmd(cmd: String): String? {
        return when (cmd) {
            "cls" -> {
                adapter?.datas?.clear()
                adapter?.notifyDataSetChanged()
                null
            }
            "daa" -> "dumpsys activity activities"
            "das" -> "dumpsys activity ${context.applicationContext.packageName}"
            "dass" -> "dumpsys activity services ${context.applicationContext.packageName}"
            "dms" -> "dumpsys meminfo ${context.applicationContext.packageName}"
            else -> cmd
        }
    }
}