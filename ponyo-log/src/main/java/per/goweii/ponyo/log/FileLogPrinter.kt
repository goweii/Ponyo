package per.goweii.ponyo.log

import com.tencent.mars.xlog.Xlog

object FileLogPrinter : LogPrinter {

    private var opened = false

    init {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")
    }

    fun open(
        cachePath: String,
        logPath: String,
        namePrefix: String,
        publicKey: String
    ) {
        if (opened) return
        opened = true
        Xlog.appenderOpen(
            Xlog.LEVEL_ALL, Xlog.AppednerModeAsync,
            cachePath, logPath, namePrefix, 0, publicKey
        )
        Xlog.setConsoleLogOpen(false)
        com.tencent.mars.xlog.Log.setLogImp(Xlog())
    }

    fun close() {
        if (!opened) return
        com.tencent.mars.xlog.Log.appenderClose()
    }

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        if (!opened) {
            throw IllegalStateException("FileLogPrinter not open")
        }
        when (level) {
            Ponlog.Level.ERROR -> com.tencent.mars.xlog.Log.e(tag, formatMsg(body, msg))
            Ponlog.Level.WARN -> com.tencent.mars.xlog.Log.w(tag, formatMsg(body, msg))
            Ponlog.Level.INFO -> com.tencent.mars.xlog.Log.i(tag, formatMsg(body, msg))
            Ponlog.Level.DEBUG -> com.tencent.mars.xlog.Log.d(tag, formatMsg(body, msg))
            Ponlog.Level.VERBOSE -> com.tencent.mars.xlog.Log.v(tag, formatMsg(body, msg))
            Ponlog.Level.ASSERT -> com.tencent.mars.xlog.Log.f(tag, formatMsg(body, msg))
        }
    }

    private fun formatMsg(body: LogBody, msg: String): String {
        return "${body.classInfo}:$msg"
    }
}