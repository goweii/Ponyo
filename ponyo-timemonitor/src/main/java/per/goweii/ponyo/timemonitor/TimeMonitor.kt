package per.goweii.ponyo.timemonitor

import per.goweii.ponyo.log.Ponlog
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

object TimeMonitor {
    private val timeMap = ConcurrentHashMap<String, MutableList<TimeCost>>()

    /**
     * 开始一组时间线的记录
     * 同一标记的时间线只能有一条，会移除之前已存在的
     * @param tagLine 时间线标记
     */
    fun start(tagLine: String, tagStart: String = "record start") {
        timeMap.remove(tagLine)
        timeMap[tagLine] = mutableListOf()
        record(tagLine, tagStart)
    }

    /**
     * 在对应时间线上打点
     * @param tagLine 时间线标记
     * @param tagPoint 打点标记
     */
    fun record(tagLine: String, tagPoint: String) {
        timeMap[tagLine]?.run {
            val time = System.currentTimeMillis()
            val totalCost = firstOrNull()?.let {
                time - it.timestamp
            } ?: 0
            val stepCost = lastOrNull()?.let {
                time - it.timestamp
            } ?: 0
            add(TimeCost(tagPoint, time, totalCost, stepCost).also {
                log("$tagLine:$it")
            })
        }
    }

    /**
     * 结束对应时间线
     * @param tagLine 时间线标记
     */
    fun end(tagLine: String, tagEnd: String = "record end") {
        record(tagLine, tagEnd)
        timeMap.remove(tagLine)?.let { list ->
            log(formatTimeLine(tagLine, list))
        }
    }

    private fun formatTimeLine(tagLine: String, list: List<TimeCost>): String {
        val column_tag = "tag"
        val column_timestamp = "timestamp"
        val column_total_cost = "total cost"
        val column_step_cost = "step cost"
        var l_tag = column_tag.length
        var l_timestamp = column_timestamp.length
        var l_total_cost = column_total_cost.length
        var l_step_cost = column_step_cost.length
        list.forEach {
            l_tag = max(l_tag, it.tag.length)
            l_timestamp = max(l_timestamp, it.timestamp.toString().length)
            l_total_cost = max(l_total_cost, it.totalCost.toString().length)
            l_step_cost = max(l_step_cost, it.stepCost.toString().length)
        }
        val sb = StringBuilder()
            .append("One time monitor has ended and all records printed as follows")
            .append("\n+").append("-".r(n = l_tag + l_timestamp + l_total_cost + l_step_cost + 3)).append("+")
            .append("\n|").append(" ".r(tagLine, l_tag + l_timestamp + l_total_cost + l_step_cost + 3, "")).append("|")
            .append("\n+").append("-".r(n = l_tag)).append("+").append("-".r(n = l_timestamp)).append("+")
            .append("-".r(n = l_total_cost)).append("+").append("-".r(n = l_step_cost)).append("+")
            .append("\n|").append(" ".r(column_tag, l_tag, "")).append("|")
            .append(" ".r(column_timestamp, l_timestamp, "")).append("|").append(" ".r(column_total_cost, l_total_cost, ""))
            .append("|").append(" ".r(column_step_cost, l_step_cost, "")).append("|")
            .append("\n+").append("-".r(n = l_tag)).append("+").append("-".r(n = l_timestamp)).append("+")
            .append("-".r(n = l_total_cost)).append("+").append("-".r(n = l_step_cost)).append("+")
        list.forEach {
            sb.append("\n|").append(" ".r(it.tag, l_tag, "")).append("|")
                .append(" ".r("", l_timestamp, "${it.timestamp}")).append("|")
                .append(" ".r("", l_total_cost, "${it.totalCost}")).append("|")
                .append(" ".r("", l_step_cost, "${it.stepCost}")).append("|")
        }
        sb.append("\n+").append("-".r(n = l_tag)).append("+").append("-".r(n = l_timestamp)).append("+")
            .append("-".r(n = l_total_cost)).append("+").append("-".r(n = l_step_cost)).append("+")
        return sb.toString()
    }

    private fun String.r(b: CharSequence = "", n: Int, e: CharSequence = ""): StringBuilder {
        val sb = StringBuilder(b)
        for (i in sb.length until n - e.length) {
            sb.append(this)
        }
        return sb.append(e)
    }

    private fun log(str: String) {
        Ponlog.d { str }
    }

}