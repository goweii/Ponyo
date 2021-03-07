package per.goweii.ponyo.panel.tm

import android.annotation.SuppressLint
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
        return TmHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_tm, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TmHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class TmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_tm_info by lazy { itemView.findViewById<TextView>(R.id.tv_tm_info) }

        init {
            tv_tm_info.setOnClickListener {
                datas.getOrNull(adapterPosition)?.let {
                    it.expand = !it.expand
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: TmEntity) {
            if (data.expand) {
                tv_tm_info.text = data.timeLine.toString()
            } else {
                tv_tm_info.text =
                    "${data.timeLine.tag} [${data.timeLine.points.lastOrNull()?.totalCost ?: 0}ms]"
            }
        }
    }

}