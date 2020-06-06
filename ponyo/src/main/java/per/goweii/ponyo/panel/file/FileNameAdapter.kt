package per.goweii.ponyo.panel.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class FileNameAdapter(
    private val onClickItem: (fileEntity: FileManager.FileEntity) -> Unit
) : RecyclerView.Adapter<FileNameAdapter.FileHolder>() {

    private val datas by lazy { mutableListOf<FileManager.FileEntity>() }

    fun set(data: List<FileManager.FileEntity>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_file_name, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class FileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_file_name by lazy { itemView.findViewById<TextView>(R.id.tv_file_name) }

        init {
            itemView.setOnClickListener {
                onClickItem(datas[adapterPosition])
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: FileManager.FileEntity) {
            tv_file_name.text = "${if (data.isDir) "Dir" else "File"}:${data.name}"
        }
    }

}