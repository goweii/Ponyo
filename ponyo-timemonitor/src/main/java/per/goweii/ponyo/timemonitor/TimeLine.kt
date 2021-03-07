package per.goweii.ponyo.timemonitor

import kotlin.math.max

data class TimeLine(
    val tag: String,
    val points: List<TimePoint>
){
    override fun toString(): String {
        return formatTimeLine()
    }

    private fun formatTimeLine(): String {
        val column_tag = "tag"
        val column_timestamp = "timestamp"
        val column_total_cost = "total cost"
        val column_step_cost = "step cost"
        var l_tag = column_tag.length
        var l_timestamp = column_timestamp.length
        var l_total_cost = column_total_cost.length
        var l_step_cost = column_step_cost.length
        points.forEach {
            l_tag = max(l_tag, it.tag.length)
            l_timestamp = max(l_timestamp, it.timestamp.toString().length)
            l_total_cost = max(l_total_cost, it.totalCost.toString().length)
            l_step_cost = max(l_step_cost, it.stepCost.toString().length)
        }
        val sb = StringBuilder()
            .append("+").append("-".r(n = l_tag + l_timestamp + l_total_cost + l_step_cost + 3))
            .append("+")
            .append("\n|")
            .append(" ".r(tag, l_tag + l_timestamp + l_total_cost + l_step_cost + 3, ""))
            .append("|")
            .append("\n+").append("-".r(n = l_tag)).append("+").append("-".r(n = l_timestamp))
            .append("+")
            .append("-".r(n = l_total_cost)).append("+").append("-".r(n = l_step_cost)).append("+")
            .append("\n|").append(" ".r(column_tag, l_tag, "")).append("|")
            .append(" ".r(column_timestamp, l_timestamp, "")).append("|")
            .append(" ".r(column_total_cost, l_total_cost, ""))
            .append("|").append(" ".r(column_step_cost, l_step_cost, "")).append("|")
            .append("\n+").append("-".r(n = l_tag)).append("+").append("-".r(n = l_timestamp))
            .append("+")
            .append("-".r(n = l_total_cost)).append("+").append("-".r(n = l_step_cost)).append("+")
        points.forEach {
            sb.append("\n|").append(" ".r(it.tag, l_tag, "")).append("|")
                .append(" ".r("", l_timestamp, "${it.timestamp}")).append("|")
                .append(" ".r("", l_total_cost, "${it.totalCost}")).append("|")
                .append(" ".r("", l_step_cost, "${it.stepCost}")).append("|")
        }
        sb.append("\n+").append("-".r(n = l_tag)).append("+").append("-".r(n = l_timestamp))
            .append("+")
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimeLine
        if (tag != other.tag) return false
        return true
    }

    override fun hashCode(): Int {
        return tag.hashCode()
    }
}
