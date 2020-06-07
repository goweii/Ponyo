package per.goweii.ponyo.panel.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class FileTabAdapter(
    private val onClickItem: (fileEntity: FileManager.FileEntity) -> Unit
) : RecyclerView.Adapter<FileTabAdapter.FileHolder>() {

    private val datas by lazy { mutableListOf<Selectable>() }

    data class Selectable(
        var selected: Boolean,
        val fileEntity: FileManager.FileEntity
    )

    fun set(data: List<FileManager.FileEntity>) {
        datas.clear()
        val newDatas = mutableListOf<Selectable>()
        data.forEach {
            newDatas.add(Selectable(false, it))
        }
        datas.addAll(newDatas)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_tab, parent, false)
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
                onClickItem(selectable.fileEntity)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: Selectable) {
            tv_name.text = data.fileEntity.name
            tv_name.isSelected = data.selected
        }
    }

}