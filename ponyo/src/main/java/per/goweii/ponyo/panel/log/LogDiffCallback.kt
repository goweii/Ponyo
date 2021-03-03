package per.goweii.ponyo.panel.log

import androidx.recyclerview.widget.DiffUtil
import per.goweii.ponyo.log.LogLine

class LogDiffCallback(
    private val oldList: List<LogLine>,
    private val newList: List<LogLine>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].message == newList[newItemPosition].message
    }
}