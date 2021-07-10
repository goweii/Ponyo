package per.goweii.ponyo.panel.shell

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class ShellAdapter : RecyclerView.Adapter<ShellAdapter.ShellHolder>() {
    private val datas = arrayListOf<String>()

    fun add(data: String) {
        datas.add(data)
        notifyItemInserted(datas.lastIndex)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShellHolder {
        return ShellHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.ponyo_item_shell,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ShellHolder, position: Int) {
        val data = datas[position]
        holder.bind(data)
    }

    class ShellHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_shell: TextView = itemView.findViewById(R.id.tv_shell)

        fun bind(data: String) {
            tv_shell.text = data
        }
    }
}