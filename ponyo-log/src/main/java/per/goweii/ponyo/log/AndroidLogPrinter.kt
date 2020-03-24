package per.goweii.ponyo.log

import android.util.Log

class AndroidLogPrinter : LogPrinter {
    override fun print(level: Ponlog.Level, tag: String, msg: String) {
        Log.println(level.priority, tag, msg)
    }

    private val Ponlog.Level.priority: Int
        get() = when (this) {
            Ponlog.Level.ERROR -> Log.ERROR
            Ponlog.Level.WARN -> Log.WARN
            Ponlog.Level.INFO -> Log.INFO
            Ponlog.Level.DEBUG -> Log.DEBUG
            Ponlog.Level.VERBOSE -> Log.VERBOSE
        }
}