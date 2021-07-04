package per.goweii.ponyo.log

import android.os.Handler
import android.os.Looper
import android.os.Message

class CatchHandler(
    private val onPublish: (List<LogLine>) -> Unit
) : Handler(Looper.getMainLooper()) {
    @Suppress("UNCHECKED_CAST")
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_PUBLISH -> onPublish(msg.obj as List<LogLine>)
            MESSAGE_RESTART -> Logcat.resume()
        }
    }

    @Synchronized
    fun publish(lines: Collection<LogLine>) {
        val message = Message.obtain()
        message.what = MESSAGE_PUBLISH
        message.obj = ArrayList(lines)
        sendMessage(message)
    }

    @Synchronized
    fun restart() {
        val message = Message.obtain()
        message.what = MESSAGE_RESTART
        sendMessageDelayed(message, 100)
    }

    companion object {
        private const val MESSAGE_PUBLISH = 1001
        private const val MESSAGE_RESTART = 1002
    }
}