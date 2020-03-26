package per.goweii.ponyo.timemonitor

data class TimeCost(
    val tag: String,
    val timestamp: Long,
    val totalCost: Long,
    val stepCost: Long
) {
    override fun toString(): String {
        return "{tag=$tag,timestamp=$timestamp,totalCost=$totalCost,stepCost=$stepCost}"
    }
}