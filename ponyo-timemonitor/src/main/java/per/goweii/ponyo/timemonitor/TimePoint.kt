package per.goweii.ponyo.timemonitor

data class TimePoint(
    val tag: String,
    val timestamp: Long,
    val totalCost: Long,
    val stepCost: Long
) {
    override fun toString(): String {
        return "tag=$tag|timestamp=$timestamp|totalCost=$totalCost|stepCost=$stepCost"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TimePoint
        if (tag != other.tag) return false
        return true
    }

    override fun hashCode(): Int {
        return tag.hashCode()
    }
}