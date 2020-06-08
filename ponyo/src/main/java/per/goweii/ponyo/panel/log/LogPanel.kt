package per.goweii.ponyo.panel.log

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class LogPanel : BasePanel() {

    override fun getPanelLayoutRes(): Int = R.layout.panel_log

    override fun getPanelName(): String = "日志"

    @SuppressLint("SetTextI18n")
    override fun onPanelViewCreated(view: View) {
        val srl_log = view.findViewById<SmartRefreshLayout>(R.id.srl_log)
        srl_log.setOnRefreshListener {
            val success = LogManager.lastPage()
            srl_log.finishRefresh(success)
        }
        srl_log.setOnLoadMoreListener {
            LogManager.nextPage()
            srl_log.finishLoadMore()
        }
        val rv_log = view.findViewById<RecyclerView>(R.id.rv_log)
        val cb_a = view.findViewById<CheckBox>(R.id.cb_a)
        val cb_e = view.findViewById<CheckBox>(R.id.cb_e)
        val cb_w = view.findViewById<CheckBox>(R.id.cb_w)
        val cb_d = view.findViewById<CheckBox>(R.id.cb_d)
        val cb_i = view.findViewById<CheckBox>(R.id.cb_i)
        val cb_v = view.findViewById<CheckBox>(R.id.cb_v)
        val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            LogManager.notifyLevel(
                cb_a.isChecked,
                cb_e.isChecked,
                cb_w.isChecked,
                cb_d.isChecked,
                cb_i.isChecked,
                cb_v.isChecked
            )
        }
        cb_e.setOnCheckedChangeListener(listener)
        cb_w.setOnCheckedChangeListener(listener)
        cb_d.setOnCheckedChangeListener(listener)
        cb_i.setOnCheckedChangeListener(listener)
        cb_v.setOnCheckedChangeListener(listener)
        val et_tag = view.findViewById<EditText>(R.id.et_tag)
        et_tag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                LogManager.notifyTag(s.toString())
            }
        })
        val et_search = view.findViewById<EditText>(R.id.et_search)
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                LogManager.notifySearch(s.toString())
            }
        })
        val fl_dialog = view.findViewById<FrameLayout>(R.id.fl_dialog)
        val tv_dialog_copy = view.findViewById<TextView>(R.id.tv_dialog_copy)
        val tv_dialog_thread = view.findViewById<TextView>(R.id.tv_dialog_thread)
        val tv_dialog_tag = view.findViewById<TextView>(R.id.tv_dialog_tag)
        val tv_dialog_class = view.findViewById<TextView>(R.id.tv_dialog_class)
        val tv_dialog_file = view.findViewById<TextView>(R.id.tv_dialog_file)
        fl_dialog.setOnClickListener {
            fl_dialog.visibility = View.GONE
        }
        fl_dialog.visibility = View.GONE

        val tv_more = view.findViewById<TextView>(R.id.tv_more)

        LogManager.attachTo(rv_log, tv_more) { logEntity ->
            fl_dialog.visibility = View.VISIBLE
            tv_dialog_copy.setOnClickListener {
                val cm = it.context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("text", logEntity.toString()))
                fl_dialog.visibility = View.GONE
            }
            tv_dialog_thread.text = "Thread:${logEntity.body.threadName}"
            tv_dialog_thread.setOnClickListener {
                et_search.setText(logEntity.body.threadName)
                fl_dialog.visibility = View.GONE
            }
            tv_dialog_tag.text = "TAG:${logEntity.tag}"
            tv_dialog_tag.setOnClickListener {
                et_tag.setText(logEntity.tag)
                fl_dialog.visibility = View.GONE
            }
            tv_dialog_class.text = "Class:${logEntity.body.className}"
            tv_dialog_class.setOnClickListener {
                et_search.setText(logEntity.body.className)
                fl_dialog.visibility = View.GONE
            }
            tv_dialog_file.text = "File:${logEntity.body.fileName}"
            tv_dialog_file.setOnClickListener {
                et_search.setText(logEntity.body.fileName)
                fl_dialog.visibility = View.GONE
            }
        }
    }

    override fun onFirstVisible() {
    }

    override fun onGone() {
    }
}