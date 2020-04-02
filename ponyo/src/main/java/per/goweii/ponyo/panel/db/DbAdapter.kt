package per.goweii.ponyo.panel.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class DbAdapter : RecyclerView.Adapter<DbAdapter.DbHolder>() {

    private val datas by lazy { mutableListOf<String>() }

    fun set(data: List<String>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DbHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_db, parent, false)
        return DbHolder(view)
    }

    override fun onBindViewHolder(holder: DbHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class DbHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_db_value by lazy { itemView.findViewById<TextView>(R.id.tv_db_value) }

        fun bindData(data: String) {
            tv_db_value.text = data
        }
    }

}