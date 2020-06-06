package per.goweii.ponyo.panel.sp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class SpAdapter : RecyclerView.Adapter<SpAdapter.SpHolder>() {

    private val datas by lazy { mutableListOf<SpManager.Sp>() }

    fun set(data: List<SpManager.Sp>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sp, parent, false)
        return SpHolder(view)
    }

    override fun onBindViewHolder(holder: SpHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class SpHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_sp_key by lazy { itemView.findViewById<TextView>(R.id.tv_sp_key) }
        private val tv_sp_value by lazy { itemView.findViewById<TextView>(R.id.tv_sp_value) }

        fun bindData(data: SpManager.Sp) {
            tv_sp_key.text = data.key
            tv_sp_value.text = data.value
        }
    }

}