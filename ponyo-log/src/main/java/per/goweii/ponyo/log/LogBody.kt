package per.goweii.ponyo.log

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

data class LogBody (
    val timestamp: Long,
    val threadName: String,
    val fileName: String,
    val className: String,
    val methodName: String,
    val lineNumber: Int
) {
    val fileInfo = "($fileName:$lineNumber)"
    val classInfo = "$className.$methodName$fileInfo"
    val timeFormat = sdf.format(Date(timestamp))

    override fun toString(): String {
        return "$timeFormat$[$threadName]$classInfo"
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        internal fun build(
            invokeClass: Class<*>?,
            bridgeCount: Int
        ): LogBody {
            val stackTrace = Thread.currentThread().stackTrace
            var invokeIndex = 0
            var foundPonyo = false
            for (index in 2 until stackTrace.size) {
                val stackTraceElement = stackTrace[index]
                if (stackTraceElement.className.startsWith(Ponlog::class.java.name)) {
                    foundPonyo = true
                    invokeIndex = index
                } else {
                    if (foundPonyo) {
                        break
                    }
                }
            }
            var foundInvoke = false
            invokeClass?.let {
                for (index in invokeIndex until stackTrace.size) {
                    val stackTraceElement = stackTrace[index]
                    if (stackTraceElement.className == invokeClass.name) {
                        foundInvoke = true
                        invokeIndex = index
                    } else {
                        if (foundInvoke) {
                            break
                        }
                    }
                }
            }
            var caller: StackTraceElement? = null
            val callerIndex = invokeIndex + 1
            if (callerIndex + bridgeCount in stackTrace.indices) {
                caller = stackTrace[callerIndex + bridgeCount]
            } else if (callerIndex in stackTrace.indices) {
                caller = stackTrace[callerIndex]
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