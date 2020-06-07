package per.goweii.ponyo.panel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class PanelTabAdapter(
    private val onClickItem: (index: Int) -> Unit
) : RecyclerView.Adapter<PanelTabAdapter.TabHolder>() {

    private val datas by lazy { mutableListOf<Selectable>() }

    data class Selectable(
        var selected: Boolean,
        val panel: IPanel
    )

    fun set(data: List<IPanel>) {
        datas.clear()
        val newDatas = mutableListOf<Selectable>()
        data.forEach {
            newDatas.add(Selectable(false, it))
        }
        datas.addAll(newDatas)
        notifyDataSetChanged()
    }

    fun select(index: Int) {
        datas.forEachIndexed { i, selectable ->
            selectable.selected = i == index
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tab, parent, false)
        return TabHolder(view)
    }

    override fun onBindViewHolder(holder: TabHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class TabHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_name by lazy { itemView.findViewById<TextView>(R.id.tv_name) }

        init {
            itemView.setOnClickListener {
                onClickItem(adapterPosition)
            }
        }

        fun bindData(data: Selectable) {
            tv_name.text = data.panel.getPanelName()
            tv_name.isSelected = data.selected
        }
    }

}