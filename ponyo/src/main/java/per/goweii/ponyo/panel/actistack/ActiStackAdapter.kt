package per.goweii.ponyo.panel.actistack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class ActiStackAdapter : RecyclerView.Adapter<ActiStackAdapter.ActiHolder>() {

    private val datas by lazy { mutableListOf<String>() }

    fun set(data: List<String>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_db, parent, false)
        return ActiHolder(view)
    }

    override fun onBindViewHolder(holder: ActiHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class ActiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_db_value by lazy { itemView.findViewById<TextView>(R.id.tv_db_value) }

        fun bindData(data: String) {
            tv_db_value.text = data
        }
    }

    inner class FragHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_db_value by lazy { itemView.findViewById<TextView>(R.id.tv_db_value) }

        fun bindData(data: String) {
            tv_db_value.text = data
        }
    }

}