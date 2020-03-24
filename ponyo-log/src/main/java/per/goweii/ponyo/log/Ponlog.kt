package per.goweii.ponyo.log

object Ponlog {

    enum class Level(val value: Int) {
        ERROR(1 shl 0),
        WARN(1 shl 1),
        INFO(1 shl 2),
        DEBUG(1 shl 3),
        VERBOSE(1 shl 4),
    }

    private var filter: Int = setLevel(Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.VERBOSE)
    private var perLogMaxLength: Int = 4 * 1024
    private val androidLogPrinter: LogPrinter = AndroidLogPrinter()
    private val logPrinterList = mutableListOf<LogPrinter>()

    fun setLevel(vararg levels: Level): Int {
        filter = 0
        levels.forEach { filter = filter or it.value }
        return filter
    }

    fun addLevel(vararg levels: Level): Int {
        levels.forEach { filter = filter or it.value }
        return filter
    }

    fun removeLevel(vararg levels: Level): Int {
        levels.forEach { filter = filter xor it.value }
        return filter
    }

    fun containLevel(level: Level): Boolean {
        return level.value and filter != 0
    }

    fun setPerLogMaxLength(perLogMaxLength: Int) {
        this.perLogMaxLength = perLogMaxLength
    }

    fun setJsonFormatter(jsonFormatter: JsonFormatter?) {
        LogFormatter.jsonFormatter = jsonFormatter
    }

    fun addLogPrinter(logPrinter: LogPrinter) {
        this.logPrinterList.add(logPrinter)
    }

    fun removeLogPrinter(logPrinter: LogPrinter) {
        this.logPrinterList.remove(logPrinter)
    }

    inline fun v(tag: String? = null, msg: () -> Any?) {
        log(Level.VERBOSE, tag, msg)
    }

    inline fun d(tag: String? = null, msg: () -> Any?) {
        log(Level.DEBUG, tag, msg)
    }

    inline fun i(tag: String? = null, msg: () -> Any?) {
        log(Level.INFO, tag, msg)
    }

    inline fun w(tag: String? = null, msg: () -> Any?) {
        log(Level.WARN, tag, msg)
    }

    inline fun e(tag: String? = null, msg: () -> Any?) {
        log(Level.ERROR, tag, msg)
    }

    inline fun log(level: Level, tag: String? = null, msg: () -> Any?) {
        if (containLevel(level)) {
            log(level, tag, msg.invoke())
        }
    }

    fun log(level: Level, tag: String? = null, msg: Any?) {
        print(level, tag, msg)
    }

    private fun print(level: Level, tag: String?, msg: Any?) {
        val logBody = LogBody.build(javaClass)
        val logTag = tag ?: logBody.className
        val logMsg = LogFormatter.object2String(msg)
        if (logMsg.length > perLogMaxLength) {
            for (i in 0..logMsg.length / perLogMaxLength) {
                val start = i * perLogMaxLength
                var end = (i + 1) * perLogMaxLength
                end = if (end > logMsg.length) logMsg.length else end
                print(level, logTag, logBody, logMsg.substring(start, end))
            }
        } else {
            print(level, logTag, logBody, logMsg)
        }
    }

    private fun print(level: Level, tag: String, body: LogBody, msg: String) {
        val logMsg =
            "${body.className}.${body.methodName}(${body.fileName}:${body.lineNumber}):$msg"
        androidLogPrinter.print(level, tag, logMsg)
        logPrinterList.forEach {
            it.print(level, tag, logMsg)
        }
    }

}