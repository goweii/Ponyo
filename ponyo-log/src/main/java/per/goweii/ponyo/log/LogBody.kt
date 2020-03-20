package per.goweii.ponyo.log

data class LogBody private constructor(
    val threadName: String,
    val fileName: String,
    val className: String,
    val methodName: String,
    val lineNumber: Int
) {
    companion object {
        fun build(): LogBody {
            val stackTrace = Throwable().stackTrace
            var caller: StackTraceElement? = null
            var find = false
            for (i in 1 until stackTrace.size){
                val stackTraceElement = stackTrace[i]
                val cls1 = stackTraceElement.className
                val cls2 = Ponlog::class.java.name
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
            val className = caller?.getClsName() ?: "Unknown"
            val methodName = caller?.methodName ?: "unknown"
            val lineNumber = caller?.lineNumber ?: -1
            return LogBody(threadName, fileName, className, methodName, lineNumber)
        }

        private fun StackTraceElement.getClsName(): String {
            val fileName = fileName
            if (fileName != null) {
                val index = fileName.indexOf('.')
                return if (index == -1) fileName else fileName.substring(0, index)
            }
            var className = className
            val classNameInfo =
                className.split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (classNameInfo.isNotEmpty()) {
                className = classNameInfo[classNameInfo.size - 1]
            }
            val index = className.indexOf('$')
            if (index != -1) {
                className = className.substring(0, index)
            }
            return className
        }
    }
}