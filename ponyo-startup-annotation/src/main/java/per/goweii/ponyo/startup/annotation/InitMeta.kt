package per.goweii.ponyo.startup.annotation

data class InitMeta(
    val className: String,
    val activities: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as InitMeta
        if (className != other.className) return false
        return true
    }

    override fun hashCode(): Int {
        return className.hashCode()
    }
}