package per.goweii.ponyo.panel.shell

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class ShellAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val datas = arrayListOf<ShellEntity>()

    fun addInput(data: ShellInputEntity) {
        datas.add(data)
        notifyItemInserted(datas.lastIndex)
    }

    fun addOutput(data: ShellOutputEntity) {
        datas.add(data)
        notifyItemInserted(datas.lastIndex)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        val data = datas[position]
        return when (data.itemType) {
            ShellInputEntity.ITEM_TYPE -> ShellInputEntity.ITEM_TYPE
            ShellOutputEntity.ITEM_TYPE -> ShellOutputEntity.ITEM_TYPE
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ShellInputEntity.ITEM_TYPE -> ShellInputHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.ponyo_item_shell_input,
                    parent,
                    false
                )
            )
            ShellOutputEntity.ITEM_TYPE -> ShellOutputHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.ponyo_item_shell_output,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = datas[position]
        when (holder.itemViewType) {
            ShellInputEntity.ITEM_TYPE -> {
                holder as ShellInputHolder
                data as ShellInputEntity
                holder.bind(data)
            }
            ShellOutputEntity.ITEM_TYPE -> {
                holder as ShellOutputHolder
                data as ShellOutputEntity
                holder.bind(data)
            }
            else -> throw IllegalStateException()
        }
    }

    class ShellInputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_shell_input: TextView = itemView.findViewById(R.id.tv_shell_input)

        fun bind(data: ShellInputEntity) {
            tv_shell_input.text = data.input
        }
    }

    class ShellOutputHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_shell_output: TextView = itemView.findViewById(R.id.tv_shell_output)

        fun bind(data: ShellOutputEntity) {
            tv_shell_output.setText(data.output, TextView.BufferType.EDITABLE)
        }
    }
}