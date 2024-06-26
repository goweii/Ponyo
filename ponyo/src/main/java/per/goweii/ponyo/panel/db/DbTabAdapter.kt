package per.goweii.ponyo.panel.db

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class DbTabAdapter(
    private val onClickItem: (db: DbManager.Db) -> Unit
) : RecyclerView.Adapter<DbTabAdapter.FileHolder>() {

    private val datas by lazy { mutableListOf<Selectable>() }

    data class Selectable(
        var selected: Boolean,
        val db: DbManager.Db
    )

    fun get() = datas

    fun set(data: List<DbManager.Db>, selectIndex: Int = -1) {
        datas.clear()
        val newDatas = mutableListOf<Selectable>()
        data.forEachIndexed { index, db ->
            newDatas.add(Selectable(selectIndex == index, db))
        }
        datas.addAll(newDatas)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_tab, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class FileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_name by lazy { itemView.findViewById<TextView>(R.id.tv_name) }

        init {
            itemView.setOnClickListener {
                datas.forEach { it.selected = false }
                val selectable = datas[adapterPosition]
                selectable.selected = true
                notifyDataSetChanged()
                onClickItem(selectable.db)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: Selectable) {
            tv_name.text = data.db.name
            tv_name.isSelected = data.selected
        }
    }

}