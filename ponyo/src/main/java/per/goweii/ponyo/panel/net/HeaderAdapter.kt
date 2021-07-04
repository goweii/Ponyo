package per.goweii.ponyo.panel.net

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class HeaderAdapter(
    headers: Map<String, String>
) : RecyclerView.Adapter<HeaderAdapter.Holder>() {
    private val datas = headers.toList()

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_header, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_name by lazy { itemView.findViewById<TextView>(R.id.tv_name) }
        private val tv_value by lazy { itemView.findViewById<TextView>(R.id.tv_value) }

        fun bindData(data: Pair<String, String>) {
            tv_name.text = data.first
            tv_value.text = data.second
        }
    }

}