package per.goweii.ponyo.panel.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class FileNaviAdapter(
    private val onClickItem: (fileEntity: FileManager.FileEntity) -> Unit
) : RecyclerView.Adapter<FileNaviAdapter.FileHolder>() {

    private val datas by lazy { mutableListOf<FileManager.FileEntity>() }

    fun get(): MutableList<FileManager.FileEntity> {
        return datas
    }

    fun set(data: List<FileManager.FileEntity>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    fun add(fileEntity: FileManager.FileEntity) {
        datas.add(fileEntity)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_file_navi, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class FileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_dir_name by lazy { itemView.findViewById<TextView>(R.id.tv_dir_name) }

        init {
            itemView.setOnClickListener {
                onClickItem(datas[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: FileManager.FileEntity) {
            tv_dir_name.text = "${data.name}/"
        }
    }

}