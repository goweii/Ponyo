package per.goweii.ponyo.timemonitor

import android.util.Log
import java.util.concurrent.ConcurrentHashMap

object TimeMonitor {
    private val timeMap = ConcurrentHashMap<String, MutableList<TimePoint>>()

    private var timeLineEndListeners = arrayListOf<OnTimeLineEndListener>()

    fun registerTimeLineEndListener(listener: OnTimeLineEndListener) {
        timeLineEndListeners.add(listener)
    }

    fun unregisterTimeLineEndListener(listener: OnTimeLineEndListener) {
        timeLineEndListeners.remove(listener)
    }

    /**
     * 开始一组时间线的记录
     * 同一标记的时间线只能有一条，会移除之前已存在的
     * @param lineTag 时间线标记
     */
    fun start(lineTag: String, pointTag: String = "record start") {
        timeMap.remove(lineTag)
        timeMap[lineTag] = mutableListOf()
        record(lineTag, pointTag)
    }

    /**
     * 在对应时间线上打点
     * @param lineTag 时间线标记
     * @param pointTag 打点标记
     */
    fun record(lineTag: String, pointTag: String) {
        timeMap[lineTag]?.run {
            val time = System.currentTimeMillis()
            val totalCost = firstOrNull()?.let {
                time - it.timestamp
            } ?: 0
            val stepCost = lastOrNull()?.let {
                time - it.timestamp
            } ?: 0
            add(TimePoint(pointTag, time, totalCost, stepCost))
        }
    }

    /**
     * 结束对应时间线
     * @param lineTag 时间线标记
     */
    fun end(lineTag: String, pointTag: String = "record end") {
        record(lineTag, pointTag)
        timeMap.remove(lineTag)?.let { timePoints ->
            val timeLine = TimeLine(lineTag, timePoints)
            if (timeLineEndListeners.isEmpty()) {
                Log.d("Ponyo-TimeMonitor", timeLine.toString())
            } else {
                timeLineEndListeners.forEach {
                    it.onEnd(timeLine)
                }
            }
        }
    }

}