package per.goweii.ponyo.startup.annotation

data class InitMeta(
    val className: String,
    val async: Boolean,
    val priority: Int,
    val activities: Array<String>,
    val fragments: Array<String>
) {
    var isInitialized: Boolean = false

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

    companion object {
        fun toSpValue(initMetas: Set<InitMeta>): String {
            val sb = StringBuilder()
            initMetas.forEachIndexed { index, initMeta ->
                if (index > 0) sb.append("\n")
                sb.append("className=${initMeta.className}")
                sb.append("\nasync=${initMeta.async}")
                sb.append("\npriority=${initMeta.priority}")
                initMeta.activities.forEach { activity ->
                    sb.append("\nactivity=${activity}")
                }
                initMeta.fragments.forEach { fragment ->
                    sb.append("\nfragment=${fragment}")
                }
            }
            return sb.toString()
        }

        @Throws(IllegalArgumentException::class)
        fun fromSpValue(string: String): Set<InitMeta> {
            val set = mutableSetOf<InitMeta>()

            var className = ""
            var async = ""
            var priority = ""
            val activities = arrayListOf<String>()
            val fragments = arrayListOf<String>()

            fun commitAndReset() {
                if (className.isEmpty()) throw IllegalArgumentException()
                val asyncBoolean = when (async) {
                    "true" -> true
                    "false" -> true
                    else -> throw IllegalArgumentException()
                }
                val priorityInt = priority.toIntOrNull() ?: throw IllegalArgumentException()
                val initMeta = InitMeta(
                    className, asyncBoolean, priorityInt,
                    activities.toTypedArray(),
                    fragments.toTypedArray()
                )
                set.add(initMeta)
                className = ""
                async = ""
                priority = ""
                activities.clear()
                fragments.clear()
            }

            val lines = string.reader().readLines()
            for (line in lines) {
                val index = line.indexOf("=")
                if (index == -1) throw IllegalArgumentException()
                val key = line.substring(0, index)
                val value = line.substring(index + 1, line.length)
                when (key) {
                    "className" -> {
                        if (className.isNotEmpty())
                            commitAndReset()
                        className = value
                    }
                    "async" -> async = value
                    "priority" -> priority = value
                    "activity" -> activities.add(value)
                    "fragment" -> fragments.add(value)
                    else -> throw IllegalArgumentException()
                }
            }
            commitAndReset()
            return set
        }
    }
}