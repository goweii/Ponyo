package per.goweii.ponyo.panel.net

import android.annotation.SuppressLint
import android.text.format.Formatter
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yuyh.jsonviewer.library.JsonRecyclerView
import com.yuyh.jsonviewer.library.adapter.BaseJsonViewerAdapter
import per.goweii.ponyo.R
import per.goweii.ponyo.dialog.FrameDialog
import per.goweii.ponyo.net.data.IDataPoolHandleImpl
import per.goweii.ponyo.net.data.NetworkFeedBean
import per.goweii.ponyo.panel.Panel
import per.goweii.ponyo.panel.file.fullLength
import per.goweii.ponyo.widget.HScrollView

class NetPanel : Panel() {
    private var rv: RecyclerView? = null
    private var requestListAdapter: RequestListAdapter? = null

    override fun getPanelName(): String {
        return "网络"
    }

    override fun getLayoutRes(): Int {
        return R.layout.ponyo_panel_net
    }

    override fun onCreated(view: View) {
        super.onCreated(view)
        rv = view.findViewById(R.id.ponyo_panel_net_rv)
        rv?.layoutManager = LinearLayoutManager(context)
        requestListAdapter = RequestListAdapter()
        requestListAdapter?.onItemClick { bean ->
            showDetailsDialog(bean)
        }
        rv?.adapter = requestListAdapter
    }

    override fun onVisible(view: View) {
        super.onVisible(view)
        IDataPoolHandleImpl.getInstance()
            .networkFeedMap
            ?.values
            ?.sortedBy { it.createTime }
            ?.let {
                requestListAdapter?.set(it)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun showDetailsDialog(bean: NetworkFeedBean) {
        makeDialog()
            ?.animStyle(FrameDialog.AnimStyle.Bottom)
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
                rv_resp_headers.adapter = HeaderAdapter(bean.requestHeadersMap)
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
}