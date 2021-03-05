package per.goweii.ponyo.panel.log

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class LogPidAdapter(
    pids: Map<String, Int>,
    private var selectedPid: Int
) : RecyclerView.Adapter<LogPidAdapter.Holder>() {
    private val data = pids.toList()
    var onClick: ((Pair<String, Int>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.ponyo_item_pid, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val pidName = data[position].first
        val pid = data[position].second
        holder.tv_name.text = "$pidName"
        holder.tv_pid.text = "$pid"
        holder.tv_name.isSelected = pid == selectedPid
        holder.tv_pid.isSelected = pid == selectedPid
        holder.itemView.setOnClickListener {
            selectedPid = pid
            notifyDataSetChanged()
            onClick?.invoke(data[position])
        }
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_name: TextView = view.findViewById(R.id.ponyo_item_pid_tv_name)
        val tv_pid: TextView = view.findViewById(R.id.ponyo_item_pid_tv_pid)
    }
}