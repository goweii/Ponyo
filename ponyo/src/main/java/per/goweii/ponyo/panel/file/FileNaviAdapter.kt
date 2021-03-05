package per.goweii.ponyo.panel.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import java.io.File

class FileNaviAdapter(
    private val onClickItem: (File) -> Unit
) : RecyclerView.Adapter<FileNaviAdapter.FileHolder>() {

    private val datas by lazy { mutableListOf<File>() }

    fun get(): MutableList<File> {
        return datas
    }

    fun getLast(): File? {
        return datas.lastOrNull()
    }

    fun set(data: List<File>) {
        datas.clear()
        datas.addAll(data)
        notifyDataSetChanged()
    }

    fun add(file: File) {
        datas.add(file)
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
        fun bindData(data: File) {
            tv_dir_name.text = "${data.name(tv_dir_name.context)}/"
        }
    }

}