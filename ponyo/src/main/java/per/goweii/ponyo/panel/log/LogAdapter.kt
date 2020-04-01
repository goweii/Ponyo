package per.goweii.ponyo.panel.log

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.log.Ponlog
import java.text.SimpleDateFormat

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
@SuppressLint("SimpleDateFormat")
class LogAdapter : RecyclerView.Adapter<LogAdapter.LogHolder>() {

    private val simpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("HH:mm:ss") }

    private val datas by lazy { mutableListOf<LogEntity>() }

    fun add(start: Int = datas.size, data: List<LogEntity>) {
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

    fun set(data: List<LogEntity>) {
        val oldList = mutableListOf<LogEntity>()
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogHolder(view)
    }

    override fun onBindViewHolder(holder: LogHolder, position: Int) {
        val data = datas[position]
        holder.bindData(data)
    }

    inner class LogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tv_log_tag by lazy { itemView.findViewById<TextView>(R.id.tv_log_tag) }
        private val tv_log_call by lazy { itemView.findViewById<TextView>(R.id.tv_log_call) }
        private val tv_log_msg by lazy { itemView.findViewById<TextView>(R.id.tv_log_msg) }

        @SuppressLint("SetTextI18n")
        fun bindData(data: LogEntity) {
            val color = when (data.level) {
                Ponlog.Level.ERROR -> itemView.context.resources.getColor(R.color.colorLogError)
                Ponlog.Level.WARN -> itemView.context.resources.getColor(R.color.colorLogWarn)
                Ponlog.Level.INFO -> itemView.context.resources.getColor(R.color.colorLogInfo)
                Ponlog.Level.DEBUG -> itemView.context.resources.getColor(R.color.colorLogDebug)
                Ponlog.Level.VERBOSE -> itemView.context.resources.getColor(R.color.colorLogVisible)
            }
            tv_log_tag.setTextColor(color)
            tv_log_call.setTextColor(color)
            tv_log_msg.setTextColor(color)
            tv_log_tag.text =
                "${simpleDateFormat.format(data.body.timestamp)} ${data.level.name}/${data.tag} ${data.body.threadName}"
            tv_log_call.text =
                "${data.body.className}.${data.body.methodName}(${data.body.fileName}:${data.body.lineNumber})"
            tv_log_msg.text = data.msg
        }
    }

}