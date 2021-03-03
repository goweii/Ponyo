package per.goweii.ponyo.panel.log

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.log.LogLine

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
class LogAdapter : RecyclerView.Adapter<LogAdapter.LogHolder>() {

    private var itemClicked: ((logEntity: LogLine) -> Unit)? = null
    private val datas by lazy { mutableListOf<LogLine>() }

    fun onItemClicked(itemClicked: (logEntity: LogLine) -> Unit) {
        this.itemClicked = itemClicked
    }

    fun add(start: Int = datas.size, data: LogLine) {
        datas.add(start, data)
        notifyItemInserted(start)
    }

    fun addAll(start: Int = datas.size, data: List<LogLine>) {
        if (data.isEmpty()) return
        datas.addAll(start, data)
        notifyItemRangeInserted(start, data.size)
    }

    fun remove(start: Int = 0, count: Int) {
        for (i in 0 until count) {
            datas.removeAt(start)
        }
        notifyItemRangeRemoved(start, count)
    }

    fun set(data: List<LogLine>) {
        val oldList = mutableListOf<LogLine>()
        oldList.addAll(datas)
        datas.clear()
        datas.addAll(data)
        val result = DiffUtil.calculateDiff(LogDiffCallback(oldList, datas))
        result.dispatchUpdatesTo(this)
    }

    fun clear() {
        val count = datas.size
        datas.clear()
        notifyItemRangeRemoved(0, count)
    }

    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_log, parent, false)
        return LogHolder(view)
    }

    override fun onBindViewHolder(holder: LogHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class LogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fl_log_root by lazy { itemView.findViewById<FrameLayout>(R.id.fl_log_root) }
        private val ll_log_simple by lazy { itemView.findViewById<LinearLayout>(R.id.ll_log_simple) }
        private val rl_log_total by lazy { itemView.findViewById<RelativeLayout>(R.id.rl_log_total) }
        private val tv_log_simple_header by lazy { itemView.findViewById<TextView>(R.id.tv_log_simple_header) }
        private val tv_log_simple_msg by lazy { itemView.findViewById<TextView>(R.id.tv_log_simple_msg) }
        private val tv_log_header by lazy { itemView.findViewById<TextView>(R.id.tv_log_header) }
        private val tv_log_msg by lazy { itemView.findViewById<TextView>(R.id.tv_log_msg) }
        private val v_log_line by lazy { itemView.findViewById<View>(R.id.v_log_line) }

        init {
            rl_log_total.setOnLongClickListener {
                datas.getOrNull(adapterPosition)?.let {
                    itemClicked?.invoke(it)
                }
                return@setOnLongClickListener true
            }
            ll_log_simple.setOnClickListener {
                datas.getOrNull(adapterPosition)?.let {
                    it.isExpanded = true
                    notifyItemChanged(adapterPosition)
                }
            }
            rl_log_total.setOnClickListener {
                datas.getOrNull(adapterPosition)?.let {
                    it.isExpanded = false
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(data: LogLine) {
            val color = when (data.level) {
                Log.ASSERT -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogAssert)
                }
                Log.ERROR -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogError)
                }
                Log.WARN -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogWarn)
                }
                Log.INFO -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogInfo)
                }
                Log.DEBUG -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogDebug)
                }
                Log.VERBOSE -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogVisible)
                }
                else -> {
                    itemView.context.resources.getColor(R.color.ponyo_colorLogVisible)
                }
            }
            if (data.isExpanded) {
                ll_log_simple.visibility = View.GONE
                rl_log_total.visibility = View.VISIBLE
                v_log_line.setBackgroundColor(color)
                tv_log_header.setTextColor(color)
                tv_log_msg.setTextColor(color)
                tv_log_header.text = "${data.logLevelText}/${data.tag} ${data.timestamp}"
                tv_log_msg.text = data.message
            } else {
                ll_log_simple.visibility = View.VISIBLE
                rl_log_total.visibility = View.GONE
                tv_log_simple_header.setTextColor(color)
                tv_log_simple_msg.setTextColor(color)
                tv_log_simple_header.text = "${data.logLevelText}/${data.tag}:"
                tv_log_simple_msg.text = data.message
            }
        }
    }

}