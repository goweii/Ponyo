package per.goweii.ponyo.log

import android.util.Log

object Ponlog2 {
    enum class Level(
        val priority: Int,
        val level: Int
    ) {
        E(Log.ERROR, 1),
        W(Log.WARN, 1 shl 1),
        I(Log.INFO, 1 shl 2),
        D(Log.DEBUG, 1 shl 3),
        V(Log.VERBOSE, 1 shl 4);
    }

    var filter: Int = 0
        private set

    fun setFilter(vararg filters: Level) {
        filter = 0
        filters.forEach {
            filter = filter or it.level
        }
    }

    fun addFilter(vararg filters: Level) {
        filters.forEach {
            filter = filter or it.level
        }
    }

    fun removeFilter(vararg filters: Level) {
        filters.forEach {
            filter = filter xor it.level
        }
    }

    inline fun v(tag: String?, msg: () -> Any?) {
        log(Level.V, tag, msg)
    }

    inline fun d(tag: String?, msg: () -> Any?) {
        log(Level.D, tag, msg)
    }

    inline fun i(tag: String?, msg: () -> Any?) {
        log(Level.I, tag, msg)
    }

    inline fun w(tag: String?, msg: () -> Any?) {
        log(Level.W, tag, msg)
    }

    inline fun e(tag: String?, msg: () -> Any?) {
        log(Level.E, tag, msg)
    }

    inline fun log(level: Level, tag: String?, msg: () -> Any?) {
        if (level.level and filter != 0) {
            log(level, tag, msg.invoke())
        }
    }

    fun log(level: Level, tag: String?, msg: Any?) {
        println(level.priority, tag, msg)
    }

    private fun println(
        priority: Int,
        tag: String?,
        contents: Any?
    ) {
        val maxLogLength = 4096
        val logBody = LogBody.build(javaClass)
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

    private fun println(priority: Int, tag: String?, body: LogBody, content: String) {
        val t = tag ?: body.className
        val msg =
            "${body.className}.${body.methodName}(${body.fileName}:${body.lineNumber})-> $content"
        Log.println(priority, t, msg)
    }
}