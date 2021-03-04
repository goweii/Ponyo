package per.goweii.ponyo.panel.tm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import java.util.ArrayList

class TmAdapter : RecyclerView.Adapter<TmAdapter.TmHolder>() {

    private val datas by lazy { mutableListOf<TmEntity>() }

    fun set(data: ArrayList<TmEntity>) {
        val oldList = mutableListOf<TmEntity>()
        oldList.addAll(datas)
        datas.clear()
        datas.addAll(data)
        val result = DiffUtil.calculateDiff(TmDiffCallback(oldList, datas))
        result.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TmHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_tm, parent, false)
        return TmHolder(view)
    }

    override fun onBindViewHolder(holder: TmHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class TmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_tm_info by lazy { itemView.findViewById<TextView>(R.id.tv_tm_info) }

        fun bindData(data: TmEntity) {
            tv_tm_info.text = data.lineInfo
        }
    }

}