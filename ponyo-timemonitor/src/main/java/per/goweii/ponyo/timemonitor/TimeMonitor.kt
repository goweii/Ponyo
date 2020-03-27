package per.goweii.ponyo.timemonitor

import per.goweii.ponyo.log.Ponlog
import java.util.concurrent.ConcurrentHashMap

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
        var l_tag = 0
        list.forEach {
            val l = it.tag.length
            if (l > l_tag) {
                l_tag = l
            }
        }
        val l_timestamp = 13
        val l_totalCost = 9
        val l_stepCost = 8
        val sb = StringBuilder()
            .append("One time monitor has ended and all records printed as follows")
            .append("\n┌").append("─".r(n = l_tag + l_timestamp + l_totalCost + l_stepCost + 3)).append("┐")
            .append("\n│").append(" ".r(tagLine, l_tag + l_timestamp + l_totalCost + l_stepCost + 3, "")).append("│")
            .append("\n├").append("─".r(n = l_tag)).append("┬").append("─".r(n = l_timestamp)).append("┬")
            .append("─".r(n = l_totalCost)).append("┬").append("─".r(n = l_stepCost)).append("┤")
            .append("\n│").append(" ".r("tag", l_tag, "")).append("│")
            .append(" ".r("timestamp", l_timestamp, "")).append("│").append(" ".r("totalCost", l_totalCost, ""))
            .append("│").append(" ".r("stepCost", l_stepCost, "")).append("│")
            .append("\n├").append("─".r(n = l_tag)).append("┼").append("─".r(n = l_timestamp)).append("┼")
            .append("─".r(n = l_totalCost)).append("┼").append("─".r(n = l_stepCost)).append("┤")
        list.forEach {
            sb.append("\n│").append(" ".r(it.tag, l_tag, "")).append("│")
                .append(" ".r("", l_timestamp, "${it.timestamp}")).append("│")
                .append(" ".r("", l_totalCost, "${it.totalCost}")).append("│")
                .append(" ".r("", l_stepCost, "${it.stepCost}")).append("│")
        }
        sb.append("\n└").append("─".r(n = l_tag)).append("┴").append("─".r(n = l_timestamp)).append("┴")
            .append("─".r(n = l_totalCost)).append("┴").append("─".r(n = l_stepCost)).append("┘")
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
        Ponlog.d("TimeMonitor") { str }
    }

}