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
            invokeClass: Class<*>,
            bridgeCount: Int
        ): LogBody {
            val stackTrace = Thread.currentThread().stackTrace
            var invokeIndex = -1
            stackTrace.forEachIndexed { index, stackTraceElement ->
                if (stackTraceElement.className == invokeClass.name) {
                    invokeIndex = index
                    return@forEachIndexed
                }
            }
            invokeIndex += bridgeCount
            var caller: StackTraceElement? = null
            if (invokeIndex + 1 in stackTrace.indices) {
                caller = stackTrace[invokeIndex + 1]
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