package per.goweii.ponyo.panel.log

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class LogPanel : BasePanel() {

    private lateinit var rv_log: RecyclerView

    override fun getPanelLayoutRes(): Int = R.layout.ponyo_panel_log

    override fun getPanelName(): String = "日志"

    @SuppressLint("SetTextI18n")
    override fun onPanelViewCreated(view: View) {
        rv_log = view.findViewById(R.id.rv_log)
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
        val tv_dialog_tag = view.findViewById<TextView>(R.id.tv_dialog_tag)
        fl_dialog.setOnClickListener {
            fl_dialog.visibility = View.GONE
        }
        fl_dialog.visibility = View.GONE
        val iv_clear = view.findViewById<ImageView>(R.id.iv_clear)
        iv_clear.setOnClickListener {
            LogManager.clear()
        }
        val iv_tag_clear = view.findViewById<ImageView>(R.id.iv_tag_clear)
        iv_tag_clear.setOnClickListener {
            et_tag.setText("")
        }
        val iv_search_clear = view.findViewById<ImageView>(R.id.iv_search_clear)
        iv_search_clear.setOnClickListener {
            et_search.setText("")
        }

        val tv_more = view.findViewById<TextView>(R.id.tv_more)

        LogManager.attachTo(rv_log, tv_more) { logLine ->
            fl_dialog.visibility = View.VISIBLE
            tv_dialog_copy.setOnClickListener {
                val cm = it.context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("text", logLine.toString()))
                fl_dialog.visibility = View.GONE
            }
            tv_dialog_tag.text = "TAG:${logLine.tag}"
            tv_dialog_tag.setOnClickListener {
                et_tag.setText(logLine.tag)
                fl_dialog.visibility = View.GONE
            }
        }
    }

    override fun onVisible(firstVisible: Boolean) {
        super.onVisible(firstVisible)
        if (firstVisible) {
            rv_log.post {
                LogManager.scrollBottom()
            }
        }
        LogManager.clearUnreadCount()
    }

    override fun onGone() {
    }
}