package per.goweii.ponyo.panel.tm

data class TmEntity (
    val lineTag: String,
    var lineInfo: String
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TmEntity
        if (lineTag != other.lineTag) return false
        return true
    }

    override fun hashCode(): Int {
        return lineTag.hashCode()
    }
}