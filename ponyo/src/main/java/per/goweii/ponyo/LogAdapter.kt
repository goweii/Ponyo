package per.goweii.ponyo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.log.Ponlog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
@SuppressLint("SimpleDateFormat")
class LogAdapter(
    private val datas: MutableList<LogManager.Log>
) : RecyclerView.Adapter<LogAdapter.LogHolder>() {

    private val simpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("HH:mm:ss") }

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
        fun bindData(data: LogManager.Log) {
            when (data.level) {
                Ponlog.Level.ERROR -> {
                    if (LogManager.e) {
                        itemView.visibility = View.VISIBLE
                    } else {
                        itemView.visibility = View.GONE
                    }
                }
                Ponlog.Level.WARN ->  {
                    if (LogManager.w) {
                        itemView.visibility = View.VISIBLE
                    } else {
                        itemView.visibility = View.GONE
                    }
                }
                Ponlog.Level.INFO ->  {
                    if (LogManager.i) {
                        itemView.visibility = View.VISIBLE
                    } else {
                        itemView.visibility = View.GONE
                    }
                }
                Ponlog.Level.DEBUG ->  {
                    if (LogManager.d) {
                        itemView.visibility = View.VISIBLE
                    } else {
                        itemView.visibility = View.GONE
                    }
                }
                Ponlog.Level.VERBOSE ->  {
                    if (LogManager.v) {
                        itemView.visibility = View.VISIBLE
                    } else {
                        itemView.visibility = View.GONE
                    }
                }
            }
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
                "${simpleDateFormat.format(data.body.timestamp)} ${data.body.threadName} ${data.level.name}/${data.tag}"
            tv_log_call.text =
                "${data.body.className}.${data.body.methodName}(${data.body.fileName}:${data.body.lineNumber})"
            tv_log_msg.text = data.msg
        }
    }

}