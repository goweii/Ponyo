package per.goweii.ponyo.panel.device

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R

class DeviceAdapter: RecyclerView.Adapter<DeviceAdapter.Holder>() {
    private val data = arrayListOf<Pair<String, String>>()

    fun set(infos: Map<String, String>) {
        data.clear()
        data.addAll(infos.toList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ponyo_item_device, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val name = data[position].first
        val value = data[position].second
        holder.tv_name.text = name
        holder.tv_value.text = value
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_name: TextView = view.findViewById(R.id.ponyo_item_device_tv_name)
        val tv_value: TextView = view.findViewById(R.id.ponyo_item_device_tv_value)
    }
}