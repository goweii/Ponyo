package per.goweii.ponyo.log

data class LogBody private constructor(
    val threadName: String,
    val fileName: String,
    val className: String,
    val methodName: String,
    val lineNumber: Int
) {
    companion object {
        fun build(logCls: Class<*>): LogBody {
            val stackTrace = Thread.currentThread().stackTrace
            var caller: StackTraceElement? = null
            var find = false
            for (i in 1 until stackTrace.size) {
                val stackTraceElement = stackTrace[i]
                val cls1 = stackTraceElement.className
                val cls2 = logCls.name
                if (cls1 == cls2) {
                    find = true
                } else {
                    if (find) {
                        caller = stackTraceElement
                        break
                    }
                }
            }
            val threadName = Thread.currentThread().name
            val fileName = caller?.fileName ?: "Unknown"
            val className = caller?.simpleClassName ?: "Unknown"
            val methodName = caller?.methodName ?: "unknown"
            val lineNumber = caller?.lineNumber ?: -1
            return LogBody(threadName, fileName, className, methodName, lineNumber)
        }

        private val StackTraceElement.simpleClassName: String
            get() {
                return className.takeIf {
                    it.contains(".")
                }?.split(".")?.dropLastWhile {
                    it.isEmpty()
                }?.takeIf {
                    it.isNotEmpty()
                }?.last()?.run {
                    val index = lastIndexOf('$')
                    if (index != -1) {
                        substring(index + 1)
                    } else {
                        this
                    }
                } ?: className
            }
    }
}