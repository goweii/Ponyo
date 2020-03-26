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
        var l1 = 0
        list.forEach {
            val l = it.tag.length
            if (l > l1) {
                l1 = l
            }
        }
        val l2 = 13
        val l3 = 9
        val l4 = 8
        val sb = StringBuilder()
            .append("one time monitor has ended and all records printed as follows")
            .append("\n┌").append("─".r(n = l1 + l2 + l3 + l4 + 3)).append("┐")
            .append("\n│").append(" ".r(tagLine, l1 + l2 + l3 + l4 + 3, "")).append("│")
            .append("\n├").append("─".r(n = l1)).append("┬").append("─".r(n = l2)).append("┬")
            .append("─".r(n = l3)).append("┬").append("─".r(n = l4)).append("┤")
            .append("\n│").append(" ".r("tag", l1, "")).append("│")
            .append(" ".r("timestamp", l2, "")).append("│").append(" ".r("totalCost", l3, ""))
            .append("│").append(" ".r("stepCost", l4, "")).append("│")
            .append("\n├").append("─".r(n = l1)).append("┼").append("─".r(n = l2)).append("┼")
            .append("─".r(n = l3)).append("┼").append("─".r(n = l4)).append("┤")
        list.forEach {
            sb.append("\n│").append(" ".r(it.tag, l1, "")).append("│")
                .append(" ".r("", l2, "${it.timestamp}")).append("│")
                .append(" ".r("", l3, "${it.totalCost}")).append("│")
                .append(" ".r("", l4, "${it.stepCost}")).append("│")
        }
        sb.append("\n└").append("─".r(n = l1)).append("┴").append("─".r(n = l2)).append("┴")
            .append("─".r(n = l3)).append("┴").append("─".r(n = l4)).append("┘")
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