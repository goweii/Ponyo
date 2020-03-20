package per.goweii.ponyo.log

import android.util.Log
import androidx.annotation.IntDef
import java.lang.IllegalArgumentException

object Ponlog {
    @IntDef(
        ERROR,
        WARN,
        INFO,
        DEBUG,
        VERBOSE
    )
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Level

    const val NONE = 0
    const val ERROR = 1
    const val WARN = 1 shl 1
    const val INFO = 1 shl 2
    const val DEBUG = 1 shl 3
    const val VERBOSE = 1 shl 4

    const val WARN_ABOVE = WARN or ERROR
    const val INFO_ABOVE = INFO or WARN_ABOVE
    const val DEBUG_ABOVE = DEBUG or INFO_ABOVE
    const val ALL = VERBOSE or DEBUG_ABOVE

    var filter: Int = ALL
    var maxLogLength: Int = 4 * 1024
    var printStackTrace: Boolean = false

    inline fun v(tag: String? = null, msg: () -> Any?) {
        log(VERBOSE, tag, msg)
    }

    inline fun d(tag: String? = null, msg: () -> Any?) {
        log(DEBUG, tag, msg)
    }

    inline fun i(tag: String? = null, msg: () -> Any?) {
        log(INFO, tag, msg)
    }

    inline fun w(tag: String? = null, msg: () -> Any?) {
        log(WARN, tag, msg)
    }

    inline fun e(tag: String? = null, msg: () -> Any?) {
        log(ERROR, tag, msg)
    }

    inline fun log(@Level level: Int, tag: String? = null, msg: () -> Any?) {
        if (level and filter != 0) {
            log(level, tag, msg.invoke())
        }
    }

    fun log(@Level level: Int, tag: String? = null, msg: Any?) {
        println(toPriority(level), tag, msg)
    }

    private fun println(
        priority: Int,
        tag: String?,
        contents: Any?
    ) {
        val logBody: LogBody? = if (printStackTrace) LogBody.build() else null
        val formatContent = LogFormatter.object2String(contents)
        if (formatContent.length > maxLogLength) {
            for (i in 0..formatContent.length / maxLogLength) {
                val start = i * maxLogLength
                var end = (i + 1) * maxLogLength
                end = if (end > formatContent.length) formatContent.length else end
                val content = formatContent.substring(start, end)
                println(priority, tag, logBody, content)
            }
        } else {
            println(priority, tag, logBody, formatContent)
        }
    }

    private fun println(priority: Int, tag: String?, logBody: LogBody?, content: String) {
        val t = tag ?: logBody?.className ?: "unknown"
        val msg = logBody?.let {
            "(${it.fileName}:${it.lineNumber}).${it.methodName}()-> $content"
        } ?: content
        Log.println(priority, t, msg)
    }

    private fun toPriority(@Level level: Int): Int {
        return when (level) {
            ERROR -> Log.ERROR
            WARN -> Log.WARN
            INFO -> Log.INFO
            DEBUG -> Log.DEBUG
            VERBOSE -> Log.VERBOSE
            else -> throw IllegalArgumentException("")
        }
    }

}