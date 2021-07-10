package per.goweii.ponyo.panel.shell

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel
import per.goweii.ponyo.shell.AppShell

class ShellPanel : Panel() {
    private var et_shell_input: EditText? = null
    private var hsv_shell_output: HorizontalScrollView? = null
    private var rv_shell_output: RecyclerView? = null

    private var adapter: ShellAdapter? = null

    override fun getPanelName(): String = "Shell"

    override fun getLayoutRes(): Int = R.layout.ponyo_panel_shell

    override fun onCreated(view: View) {
        super.onCreated(view)
        hsv_shell_output = view.findViewById(R.id.hsv_shell_output)
        rv_shell_output = view.findViewById(R.id.rv_shell_output)
        et_shell_input = view.findViewById(R.id.et_shell_input)
        rv_shell_output?.layoutManager = LinearLayoutManager(context)
        adapter = ShellAdapter()
        rv_shell_output?.adapter = adapter
        et_shell_input?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                scope?.launch {
                    withContext(Dispatchers.IO) {
                        AppShell.exec(v.text.toString())
                    }
                    v.text = ""
                }
                return@setOnEditorActionListener true
            }
            false
        }
        scope?.launch {
            withContext(Dispatchers.IO) {
                AppShell.forkShellProcess(context)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onVisible(view: View) {
        super.onVisible(view)
        scope?.launch {
            val success = withContext(Dispatchers.IO) {
                AppShell.connectShellProcess(::handleResult)
            }
            if (success) {
                et_shell_input?.requestFocus()
            }
        }
    }

    private fun handleResult(result: String) {
        scope?.launch {
            adapter?.add(result)
            val count = adapter?.itemCount ?: 0
            if (count > 0) {
                rv_shell_output?.scrollToPosition(count - 1)
                hsv_shell_output?.scrollTo(0, 0)
            }
        }
    }
}