package per.goweii.ponyo.log

import android.text.TextUtils
import android.util.Log
import java.util.regex.Pattern

class LogLine private constructor(private val originalLine: String) {
    var pid = -1
    var level = 0
    var tag: String = ""
    var message: String = ""
    var timestamp: String = ""
    var isExpanded = false

    fun sameLogLine(line: String?): Boolean {
        return TextUtils.equals(originalLine, line)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LogLine
        if (originalLine != other.originalLine) return false
        return true
    }

    override fun hashCode(): Int {
        return originalLine.hashCode()
    }

    val logLevelText: String
        get() = convertLogLevelToChar(level).toString()

    companion object {
        private const val TIMESTAMP_LENGTH = 19
        private val logPattern =
            Pattern.compile( // log level
                "(\\w)" +
                        "/" +  // tag
                        "([^(]+)" +
                        "\\(\\s*" +  // pid
                        "(\\d+)" +  // optional weird number that only occurs on ZTE blade
                        "(?:\\*\\s*\\d+)?" +
                        "\\): "
            )
        private val filterPattern =
            "ResourceType|memtrack|android.os.Debug|BufferItemConsumer|DPM.*|MDM.*|ChimeraUtils|BatteryExternalStats.*|chatty.*|DisplayPowerController|WidgetHelper|WearableService|DigitalWidget.*|^ANDR-PERF-.*".toRegex()

        fun newLogLine(originalLine: String?): LogLine? {
            if (originalLine.isNullOrEmpty()) return null
            if (!Character.isDigit(originalLine[0])) return null
            if (originalLine.length < TIMESTAMP_LENGTH) return null
            val logLine = LogLine(originalLine)
            val timestamp = originalLine.substring(0, TIMESTAMP_LENGTH - 1)
            logLine.timestamp = timestamp
            val matcher = logPattern.matcher(originalLine)
            if (matcher.find(TIMESTAMP_LENGTH)) {
                val logLevelChar = matcher.group(1)[0]
                val logText = originalLine.substring(matcher.end())
                if (logText.matches("^maxLineHeight.*|Failed to read.*".toRegex())) {
                    logLine.level = convertCharToLogLevel('V')
                } else {
                    logLine.level = convertCharToLogLevel(logLevelChar)
                }
                val tagText = matcher.group(2)
                if (tagText.matches(filterPattern)) {
                    logLine.level = convertCharToLogLevel('V')
                }
                logLine.tag = tagText
                logLine.pid = matcher.group(3).toInt()
                logLine.message = logText
            } else {
                logLine.message = originalLine
                logLine.level = -1
            }
            return logLine
        }

        private fun convertCharToLogLevel(logLevelChar: Char): Int {
            when (logLevelChar) {
                'D' -> return Log.DEBUG
                'E' -> return Log.ERROR
                'I' -> return Log.INFO
                'V' -> return Log.VERBOSE
                'W' -> return Log.WARN
                'F' -> return Log.ASSERT
            }
            return -1
        }

        private fun convertLogLevelToChar(logLevel: Int): Char {
            when (logLevel) {
                Log.DEBUG -> return 'D'
                Log.ERROR -> return 'E'
                Log.INFO -> return 'I'
                Log.VERBOSE -> return 'V'
                Log.WARN -> return 'W'
                Log.ASSERT -> return 'F'
            }
            return ' '
        }
    }

}