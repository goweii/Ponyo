package per.goweii.android.ponyo.log

import per.goweii.ponyo.log.Ponlog

object LogUtils {

    private val logger = Ponlog.create().apply {
        setFileLogPrinterEnable(true)
        setInvokeClass(LogUtils.javaClass)
    }

    fun v(tag: String? = null, msg: Any?) {
        logger.v(tag) { msg }
    }

    fun d(tag: String? = null, msg: Any?) {
        logger.d(tag) { msg }
    }
}

private val logger = Ponlog.create().apply {
    setFileLogPrinterEnable(true)
    setBridgeClassCount(1)
}

fun i(tag: String? = null, msg: Any?) {
    logger.i(tag) { msg }
}