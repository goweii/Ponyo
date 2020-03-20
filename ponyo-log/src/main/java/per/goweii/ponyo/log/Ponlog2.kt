package per.goweii.ponyo.log

import android.util.Log

object Ponlog2 {
    enum class Level(
        val level: Int
    ) {
        ERROR(1),
        WARN(1 shl 1),
        INFO(1 shl 2),
        DEBUG(1 shl 3),
        VERBOSE(1 shl 4);

        fun toPriority(): Int {
            return when (this) {
                ERROR -> Log.ERROR
                WARN -> Log.WARN
                INFO -> Log.INFO
                DEBUG -> Log.DEBUG
                VERBOSE -> Log.VERBOSE
            }
        }
    }

    var filter: Int = 0
        private set

    fun setFilter(vararg filters: Level) {
        filter = 0
        filters.forEach {
            filter = it.level or filter
        }
    }

    inline fun v(tag: String, msg: () -> Any?) {
        log(Level.VERBOSE, tag, msg)
    }

    inline fun d(tag: String, msg: () -> Any?) {
        log(Level.DEBUG, tag, msg)
    }

    inline fun i(tag: String, msg: () -> Any?) {
        log(Level.INFO, tag, msg)
    }

    inline fun w(tag: String, msg: () -> Any?) {
        log(Level.WARN, tag, msg)
    }

    inline fun e(tag: String, msg: () -> Any?) {
        log(Level.ERROR, tag, msg)
    }

    inline fun log(level: Level, tag: String, msg: () -> Any?) {
        if (level.level and filter != 0) {
            log(level, tag, msg.invoke() ?: "null")
        }
    }

    fun log(level: Level, tag: String, msg: Any) {
        //println(level.toPriority(), tag, msg)
    }
}