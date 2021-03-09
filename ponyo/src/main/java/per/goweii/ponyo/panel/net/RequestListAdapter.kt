package per.goweii.ponyo.panel.net

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.net.data.NetworkFeedBean

class RequestListAdapter : RecyclerView.Adapter<RequestListAdapter.Holder>() {
    private val datas by lazy { mutableListOf<NetworkFeedBean>() }
    private var onItemClick: ((NetworkFeedBean) -> Unit)? = null

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
        private val tv_status by lazy { itemView.findViewById<TextView>(R.id.tv_status) }
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
            tv_status.text = data.status.toString()
            tv_url.text = data.url
        }
    }

}