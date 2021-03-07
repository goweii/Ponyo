package per.goweii.ponyo.panel.tm

import per.goweii.ponyo.timemonitor.TimeLine

data class TmEntity (
    val timeLine: TimeLine
){
    var expand = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TmEntity
        if (timeLine != other.timeLine) return false
        return true
    }

    override fun hashCode(): Int {
        return timeLine.hashCode()
    }
}