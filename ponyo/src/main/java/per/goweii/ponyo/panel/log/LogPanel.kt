package per.goweii.ponyo.panel.log

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.Ponyo
import per.goweii.ponyo.R
import per.goweii.ponyo.log.LogLine
import per.goweii.ponyo.log.Logcat
import per.goweii.ponyo.log.PidUtils
import per.goweii.ponyo.panel.Panel

class LogPanel : Panel() {
    private var rv_log: RecyclerView? = null
    private var et_tag: EditText? = null
    private var et_search: EditText? = null
    private var tv_pid: TextView? = null
    private var tv_more: TextView? = null
    private var cb_a: CheckBox? = null
    private var cb_e: CheckBox? = null
    private var cb_w: CheckBox? = null
    private var cb_d: CheckBox? = null
    private var cb_i: CheckBox? = null
    private var cb_v: CheckBox? = null
    private var iv_clear: ImageView? = null
    private var iv_tag_clear: ImageView? = null
    private var iv_search_clear: ImageView? = null
    private var ll_pid: LinearLayout? = null

    override fun getLayoutRes(): Int = R.layout.ponyo_panel_log

    override fun getPanelName(): String = "日志"

    @SuppressLint("SetTextI18n")
    override fun onCreated(view: View) {
        rv_log = view.findViewById(R.id.rv_log)
        cb_a = view.findViewById(R.id.cb_a)
        cb_e = view.findViewById(R.id.cb_e)
        cb_w = view.findViewById(R.id.cb_w)
        cb_d = view.findViewById(R.id.cb_d)
        cb_i = view.findViewById(R.id.cb_i)
        cb_v = view.findViewById(R.id.cb_v)
        et_tag = view.findViewById(R.id.et_tag)
        et_search = view.findViewById(R.id.et_search)
        iv_clear = view.findViewById(R.id.iv_clear)
        iv_tag_clear = view.findViewById(R.id.iv_tag_clear)
        iv_search_clear = view.findViewById(R.id.iv_search_clear)
        ll_pid = view.findViewById(R.id.ll_pid)
        tv_pid = view.findViewById(R.id.tv_pid)
        tv_more = view.findViewById(R.id.tv_more)
        initListener()
        val pids = PidUtils.getAllPid(tv_pid!!.context).toList()
        val pid = Logcat.getPid()
        pids.find { it.second == pid }?.let {
            tv_pid?.text = it.first
        }
        LogManager.attachTo(rv_log!!, tv_more!!) { logLine ->
            showMenuDialog(logLine)
        }
    }

    override fun onVisible(view: View) {
        super.onVisible(view)
        LogManager.clearUnreadCount()
        if (isFirstVisible) {
            rv_log?.post { LogManager.scrollBottom() }
        }
    }

    private fun initListener() {
        val listener = CompoundButton.OnCheckedChangeListener { _, _ ->
            LogManager.notifyLevel(
                cb_a?.isChecked ?: false,
                cb_e?.isChecked ?: false,
                cb_w?.isChecked ?: false,
                cb_d?.isChecked ?: false,
                cb_i?.isChecked ?: false,
                cb_v?.isChecked ?: false
            )
        }
        cb_e?.setOnCheckedChangeListener(listener)
        cb_w?.setOnCheckedChangeListener(listener)
        cb_d?.setOnCheckedChangeListener(listener)
        cb_i?.setOnCheckedChangeListener(listener)
        cb_v?.setOnCheckedChangeListener(listener)
        et_tag?.doAfterTextChanged {
            LogManager.notifyTag(it?.toString() ?: "")
        }
        et_search?.doAfterTextChanged {
            LogManager.notifySearch(it?.toString() ?: "")
        }
        iv_clear?.setOnClickListener {
            LogManager.clear()
        }
        iv_tag_clear?.setOnClickListener {
            et_tag?.setText("")
        }
        iv_search_clear?.setOnClickListener {
            et_search?.setText("")
        }
        ll_pid?.setOnClickListener {
            showSwitchPidDialog()
        }
    }

    private fun showSwitchPidDialog() {
        makeDialog()
            ?.content(R.layout.ponyo_dialog_log_pid)
            ?.bindData {
                val rv = getView<RecyclerView>(R.id.ponyo_dialog_log_pid_rv)
                rv.layoutManager = LinearLayoutManager(rv.context)
                val pids = PidUtils.getAllPid(rv.context)
                rv.adapter = LogPidAdapter(pids, Logcat.getPid()).also { adapter ->
                    adapter.onClick = { pair ->
                        tv_pid?.text = pair.first
                        Logcat.stop()
                        LogManager.clear()
                        Logcat.setPid(pair.second)
                        Logcat.start()
                        dismiss()
                    }
                }
            }
            ?.show()
    }

    private fun showMenuDialog(logLine: LogLine) {
        makeDialog()
            ?.content(R.layout.ponyo_dialog_log_menu)
            ?.bindData {
                val tv_tag = getView<TextView>(R.id.ponyo_dialog_log_meun_tv_tag)
                val tv_copy = getView<TextView>(R.id.ponyo_dialog_log_meun_tv_copy)
                tv_tag.text = "筛选TAG:${logLine.tag}"
                tv_tag.setOnClickListener {
                    et_tag?.setText(logLine.tag)
                    dismiss()
                }
                tv_copy.setOnClickListener {
                    val cm =
                        it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.primaryClip = ClipData.newPlainText("text", logLine.toString())
                    dismiss()
                }
            }
            ?.show()
    }
}