package per.goweii.ponyo.panel.net

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.net.data.NetworkFeedBean
import java.text.SimpleDateFormat
import java.util.*

class RequestAdapter : RecyclerView.Adapter<RequestAdapter.Holder>() {
    private val datas by lazy { mutableListOf<NetworkFeedBean>() }
    private var onItemClick: ((NetworkFeedBean) -> Unit)? = null

    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    private val date = Date()

    fun onItemClick(listener: (NetworkFeedBean) -> Unit) {
        onItemClick = listener
    }

    fun set(data: List<NetworkFeedBean>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_request, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_method by lazy { itemView.findViewById<TextView>(R.id.tv_method) }
        private val tv_time by lazy { itemView.findViewById<TextView>(R.id.tv_time) }
        private val v_status by lazy { itemView.findViewById<View>(R.id.v_status) }
        private val tv_url by lazy { itemView.findViewById<TextView>(R.id.tv_url) }

        init {
            itemView.setOnClickListener {
                datas.getOrNull(adapterPosition)?.let {
                    onItemClick?.invoke(it)
                }
            }
        }

        fun bindData(data: NetworkFeedBean) {
            tv_method.text = data.method
            tv_time.text = sdf.format(date.also { it.time = data.createTime })
            if (data.status in 200..299) {
                v_status.setBackgroundResource(R.color.ponyo_colorLogInfo)
            } else {
                v_status.setBackgroundResource(R.color.ponyo_colorLogError)
            }
            tv_url.text = data.url
        }
    }

}