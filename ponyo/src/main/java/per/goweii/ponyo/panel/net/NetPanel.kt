package per.goweii.ponyo.panel.net

import android.annotation.SuppressLint
import android.text.format.Formatter
import android.view.View
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyh.jsonviewer.library.JsonRecyclerView
import com.yuyh.jsonviewer.library.adapter.BaseJsonViewerAdapter
import per.goweii.ponyo.R
import per.goweii.ponyo.dialog.BottomAnimStyle
import per.goweii.ponyo.net.data.IDataPoolHandleImpl
import per.goweii.ponyo.net.data.NetworkFeedBean
import per.goweii.ponyo.net.weaknet.WeakNetworkManager
import per.goweii.ponyo.panel.Panel
import per.goweii.ponyo.widget.HScrollView

class NetPanel : Panel() {
    private var rv: RecyclerView? = null
    private var tv_net_timeout: TextView? = null
    private var tv_net_req_limit: TextView? = null
    private var tv_net_resp_limit: TextView? = null
    private var requestAdapter: RequestAdapter? = null

    override fun getPanelName(): String {
        return "网络"
    }

    override fun getLayoutRes(): Int {
        return R.layout.ponyo_panel_net
    }

    override fun onCreated(view: View) {
        super.onCreated(view)
        rv = view.findViewById(R.id.ponyo_panel_net_rv)
        val rg_net_mode = view.findViewById<RadioGroup>(R.id.rg_net_mode)
        val cv_net_timeout = view.findViewById<CardView>(R.id.cv_net_timeout)
        val cv_net_req_limit = view.findViewById<CardView>(R.id.cv_net_req_limit)
        val cv_net_resp_limit = view.findViewById<CardView>(R.id.cv_net_resp_limit)
        tv_net_timeout = view.findViewById(R.id.tv_net_timeout)
        tv_net_req_limit = view.findViewById(R.id.tv_net_req_limit)
        tv_net_resp_limit = view.findViewById(R.id.tv_net_resp_limit)
        rv?.layoutManager = LinearLayoutManager(context)
        requestAdapter = RequestAdapter()
        requestAdapter?.onItemClick { bean ->
            showDetailsDialog(bean)
        }
        rv?.adapter = requestAdapter
        rg_net_mode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_net_normal -> {
                    WeakNetworkManager.get().isActive = false
                }
                R.id.rb_net_limit -> {
                    WeakNetworkManager.get().isActive = true
                    WeakNetworkManager.get().type = WeakNetworkManager.TYPE_SPEED_LIMIT
                }
                R.id.rb_net_timeout -> {
                    WeakNetworkManager.get().isActive = true
                    WeakNetworkManager.get().type = WeakNetworkManager.TYPE_TIMEOUT
                }
                R.id.rb_net_offline -> {
                    WeakNetworkManager.get().isActive = true
                    WeakNetworkManager.get().type = WeakNetworkManager.TYPE_OFF_NETWORK
                }
            }
        }
        rg_net_mode.check(R.id.rb_net_normal)
        cv_net_timeout.setOnClickListener {
            showTimeoutChoiceDialog()
        }
        cv_net_req_limit.setOnClickListener {
            showReqLimitChoiceDialog()
        }
        cv_net_resp_limit.setOnClickListener {
            showRespLimitChoiceDialog()
        }
        tv_net_timeout?.text = WeakNetworkManager.get().timeOutMillis.toString()
        tv_net_req_limit?.text = WeakNetworkManager.get().requestSpeed.toString()
        tv_net_resp_limit?.text = WeakNetworkManager.get().responseSpeed.toString()
    }

    override fun onVisible(view: View) {
        super.onVisible(view)
        IDataPoolHandleImpl.getInstance()
            .networkFeedMap
            ?.values
            ?.sortedBy { it.createTime }
            ?.let { list ->
                requestAdapter?.set(list)
                rv?.let { rv ->
                    if (!rv.canScrollVertically(1)) {
                        if (list.isNotEmpty()) {
                            rv.scrollToPosition(list.lastIndex)
                        }
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun showDetailsDialog(bean: NetworkFeedBean) {
        makeDialog()
            ?.animStyle(BottomAnimStyle)
            ?.content(R.layout.ponyo_dialog_net_details)
            ?.bindData {
                val tv_url = getView<TextView>(R.id.ponyo_net_tv_url)
                val tv_method = getView<TextView>(R.id.ponyo_net_tv_method)
                val tv_status = getView<TextView>(R.id.ponyo_net_tv_status)
                val tv_size = getView<TextView>(R.id.ponyo_net_tv_size)
                val tv_cost = getView<TextView>(R.id.ponyo_net_tv_cost)
                val rv_req_headers = getView<RecyclerView>(R.id.ponyo_net_rv_req_headers)
                val rv_resp_headers = getView<RecyclerView>(R.id.ponyo_net_rv_resp_headers)
                val hsv_json = getView<HScrollView>(R.id.ponyo_net_hsv_json)
                val json_view = getView<JsonRecyclerView>(R.id.ponyo_net_json_view)
                val tv_text = getView<TextView>(R.id.ponyo_net_tv_text)
                tv_url.text = bean.url
                tv_method.text = bean.method
                tv_status.text = "${bean.status}"
                if (bean.status in 200..299) {
                    tv_status.setTextColor(context.resources.getColor(R.color.ponyo_colorLogInfo))
                } else {
                    tv_status.setTextColor(context.resources.getColor(R.color.ponyo_colorLogError))
                }
                tv_size.text = Formatter.formatFileSize(context, bean.size.toLong())
                tv_cost.text = "${bean.costTime} ms"
                rv_req_headers.layoutManager = LinearLayoutManager(rv_req_headers.context)
                rv_req_headers.adapter = HeaderAdapter(bean.requestHeadersMap)
                rv_resp_headers.layoutManager = LinearLayoutManager(rv_req_headers.context)
                rv_resp_headers.adapter = HeaderAdapter(bean.responseHeadersMap)
                json_view.apply {
                    BaseJsonViewerAdapter.KEY_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogAssert)
                    BaseJsonViewerAdapter.TEXT_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogInfo)
                    BaseJsonViewerAdapter.NUMBER_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogDebug)
                    BaseJsonViewerAdapter.BOOLEAN_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogError)
                    BaseJsonViewerAdapter.URL_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogInfo)
                    BaseJsonViewerAdapter.NULL_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogWarn)
                    BaseJsonViewerAdapter.BRACES_COLOR =
                        context.resources.getColor(R.color.ponyo_colorLogVisible)
                    BaseJsonViewerAdapter.TEXT_SIZE_DP =
                        context.resources.getDimension(R.dimen.ponyo_text_size_net) / context.resources.displayMetrics.scaledDensity
                }
                try {
                    json_view.bindJson(bean.body)
                    hsv_json.visibility = View.VISIBLE
                    tv_text.visibility = View.GONE
                } catch (e: Exception) {
                    tv_text.text = bean.body
                    hsv_json.visibility = View.GONE
                    tv_text.visibility = View.VISIBLE
                }
            }
            ?.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimeoutChoiceDialog() {
        val tv_net_timeout_title = view.findViewById<TextView>(R.id.tv_net_timeout_title)
        showChoiceDialog(
            tv_net_timeout_title.text.toString(),
            60_000,
            WeakNetworkManager.get().timeOutMillis.toInt(),
            "ms"
        ) {
            WeakNetworkManager.get().timeOutMillis = it.toLong()
            tv_net_timeout?.text = it.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showReqLimitChoiceDialog() {
        val tv_net_req_limit_title = view.findViewById<TextView>(R.id.tv_net_req_limit_title)
        showChoiceDialog(
            tv_net_req_limit_title.text.toString(),
            1024,
            WeakNetworkManager.get().requestSpeed.toInt(),
            "KB"
        ) {
            WeakNetworkManager.get().requestSpeed = it.toLong()
            tv_net_req_limit?.text = it.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showRespLimitChoiceDialog() {
        val tv_net_resp_limit_title = view.findViewById<TextView>(R.id.tv_net_resp_limit_title)
        showChoiceDialog(
            tv_net_resp_limit_title.text.toString(),
            1024,
            WeakNetworkManager.get().responseSpeed.toInt(),
            "KB"
        ) {
            WeakNetworkManager.get().responseSpeed = it.toLong()
            tv_net_resp_limit?.text = it.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showChoiceDialog(
        title: String,
        max: Int,
        curr: Int,
        unit: String,
        callback: (Int) -> Unit
    ) {
        makeDialog()
            ?.content(R.layout.ponyo_dialog_net_choice)
            ?.bindData {
                val tv_title = getView<TextView>(R.id.ponyo_dialog_net_choice_tv_title)
                val tv_max = getView<TextView>(R.id.ponyo_dialog_net_choice_tv_max)
                val tv_value = getView<TextView>(R.id.ponyo_dialog_net_choice_tv_value)
                val sb = getView<SeekBar>(R.id.ponyo_dialog_net_choice_sb)
                sb.max = max
                tv_title.text = title
                tv_max.text = "${sb.max}$unit"
                sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        tv_value.text = "$progress$unit"
                        callback.invoke(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
                sb.progress = curr
            }
            ?.show()
    }
}