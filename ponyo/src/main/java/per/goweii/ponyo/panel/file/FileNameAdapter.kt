package per.goweii.ponyo.panel.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import java.io.File

class FileNameAdapter(
    private val onClickItem: (File) -> Unit,
    private val onLongClickItem: (File) -> Unit
) : RecyclerView.Adapter<FileNameAdapter.FileHolder>() {

    private val datas by lazy { mutableListOf<File>() }

    fun set(data: List<File>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_file_name, parent, false)
        return FileHolder(view)
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class FileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_file_name by lazy { itemView.findViewById<TextView>(R.id.tv_file_name) }
        private val tv_file_length by lazy { itemView.findViewById<TextView>(R.id.tv_file_length) }

        init {
            itemView.setOnClickListener {
                onClickItem(datas[adapterPosition])
            }
            itemView.setOnLongClickListener {
                onLongClickItem(datas[adapterPosition])
                return@setOnLongClickListener true
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: File) {
            tv_file_name.text = "${data.name(tv_file_name.context)}${if (data.isDirectory) "/" else ""}"
            tv_file_length.text = data.fullLengthFormatted(tv_file_length.context)
        }
    }

}