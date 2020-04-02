package per.goweii.ponyo.log

data class LogBody private constructor(
    val timestamp: Long,
    val threadName: String,
    val fileName: String,
    val className: String,
    val methodName: String,
    val lineNumber: Int
) {
    companion object {
        fun build(
            invokeCls: Class<*>
        ): LogBody {
            val stackTrace = Thread.currentThread().stackTrace
            var caller: StackTraceElement? = null
            var find = false
            for (i in 1 until stackTrace.size) {
                val stackTraceElement = stackTrace[i]
                val clsName = stackTraceElement.className
                if (clsName == invokeCls.name) {
                    find = true
                } else {
                    if (find) {
                        caller = stackTraceElement
                        break
                    }
                }
            }
            val timestamp = System.currentTimeMillis()
            val threadName = Thread.currentThread().name
            val fileName = caller?.fileName ?: "Unknown"
            val className = caller?.simpleClassName ?: "Unknown"
            val methodName = caller?.methodName ?: "unknown"
            val lineNumber = caller?.lineNumber ?: -1
            return LogBody(timestamp, threadName, fileName, className, methodName, lineNumber)
        }

        private val StackTraceElement.simpleClassName: String
            get() {
                val i = className.lastIndexOf(".")
                val j = className.indexOf("$", i + 1)
                if (i >= j) return className.substring(i + 1)
                return className.substring(i + 1, j)
            }
    }
}