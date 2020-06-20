package per.goweii.ponyo.log

object Ponlog {

    enum class Level(val value: Int) {
        ASSERT(1 shl 0),
        ERROR(1 shl 1),
        WARN(1 shl 2),
        INFO(1 shl 3),
        DEBUG(1 shl 4),
        VERBOSE(1 shl 5),
        ;

        companion object {
            fun all(): Int {
                var allLevel = 0
                values().forEach { allLevel = allLevel or it.value }
                return allLevel
            }
        }
    }

    private var filter = Level.all()
    private val logPrinters = mutableListOf<LogPrinter>()
    private val logger = Logger()

    fun default() = logger

    fun create() = Logger()

    fun setJsonFormatter(jsonFormatter: JsonFormatter?) = apply {
        LogFormatter.jsonFormatter = jsonFormatter
    }

    fun openFilePrinter(
        cachePath: String,
        logPath: String,
        namePrefix: String,
        publicKey: String
    ) {
        FileLogPrinter.open(cachePath, logPath, namePrefix, publicKey)
    }

    fun closeFilePrinter() {
        FileLogPrinter.close()
    }

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

    fun addLogPrinter(logPrinter: LogPrinter) {
        this.logPrinters.add(logPrinter)
    }

    fun removeLogPrinter(logPrinter: LogPrinter) {
        this.logPrinters.remove(logPrinter)
    }

    fun v(tag: String? = null, msg: () -> Any?) {
        logger.v(tag, msg)
    }

    fun d(tag: String? = null, msg: () -> Any?) {
        logger.d(tag, msg)
    }

    fun i(tag: String? = null, msg: () -> Any?) {
        logger.i(tag, msg)
    }

    fun w(tag: String? = null, msg: () -> Any?) {
        logger.w(tag, msg)
    }

    fun e(tag: String? = null, msg: () -> Any?) {
        logger.e(tag, msg)
    }

    fun a(tag: String? = null, msg: () -> Any?) {
        logger.a(tag, msg)
    }

    fun log(level: Level, tag: String? = null, msg: () -> Any?) {
        logger.log(level, tag, msg)
    }

    fun log(level: Level, tag: String? = null, msg: Any?) {
        logger.log(level, tag, msg)
    }

    fun print(level: Level, tag: String, body: LogBody, msg: String) {
        logger.print(level, tag, body, msg)
    }

    class Logger {
        private var invokeClass: Class<*>? = null
        private var bridgeClassCount: Int = 0
        private var filter: Int = Level.all()
        private var perLogMaxLength: Int = 4 * 1024
        private var androidLogPrinter: LogPrinter? = AndroidLogPrinter
        private var fileLogPrinter: LogPrinter? = null
        private val logPrinters = mutableListOf<LogPrinter>()

        fun setInvokeClass(cls: Class<*>) = apply {
            invokeClass = cls
        }

        fun setBridgeClassCount(count: Int) = apply {
            bridgeClassCount = count
        }

        fun setLevel(vararg levels: Level) = apply {
            filter = 0
            levels.forEach { filter = filter or it.value }
        }

        fun addLevel(vararg levels: Level) = apply {
            levels.forEach { filter = filter or it.value }
        }

        fun removeLevel(vararg levels: Level) = apply {
            levels.forEach { filter = filter xor it.value }
        }

        fun setPerLogMaxLength(perLogMaxLength: Int) = apply {
            this.perLogMaxLength = perLogMaxLength
        }

        fun setAndroidLogPrinterEnable(enable: Boolean) = apply {
            androidLogPrinter = if (enable) AndroidLogPrinter else null
        }

        fun setFileLogPrinterEnable(enable: Boolean) = apply {
            fileLogPrinter = if (enable) { FileLogPrinter } else { null }
        }

        fun addLogPrinter(logPrinter: LogPrinter) = apply {
            this.logPrinters.add(logPrinter)
        }

        fun removeLogPrinter(logPrinter: LogPrinter) = apply {
            this.logPrinters.remove(logPrinter)
        }

        fun containLevel(level: Level): Boolean {
            return level.value and filter != 0
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

        inline fun a(tag: String? = null, msg: () -> Any?) {
            log(Level.ASSERT, tag, msg)
        }

        inline fun log(level: Level, tag: String? = null, msg: () -> Any?) {
            if (Ponlog.containLevel(level) && containLevel(level)) {
                log(level, tag, msg.invoke())
            }
        }

        fun log(level: Level, tag: String? = null, msg: Any?) {
            print(level, tag, msg)
        }

        private fun print(level: Level, tag: String?, msg: Any?) {
            val logBody = LogBody.build(invokeClass, bridgeClassCount)
            val logTag = tag ?: logBody.className
            val logMsg = LogFormatter.object2String(msg)
            print(level, logTag, logBody, logMsg)
        }

        fun print(level: Level, tag: String, body: LogBody, msg: String) {
            if (msg.length > perLogMaxLength) {
                for (i in 0..msg.length / perLogMaxLength) {
                    val start = i * perLogMaxLength
                    var end = (i + 1) * perLogMaxLength
                    end = if (end > msg.length) msg.length else end
                    println(level, tag, body, msg.substring(start, end))
                }
            } else {
                println(level, tag, body, msg)
            }
        }

        fun println(level: Level, tag: String, body: LogBody, msg: String) {
            androidLogPrinter?.print(level, tag, body, msg)
            fileLogPrinter?.print(level, tag, body, msg)
            logPrinters.forEach { it.print(level, tag, body, msg) }
            Ponlog.logPrinters.forEach { it.print(level, tag, body, msg) }
        }
    }
}