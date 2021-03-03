package per.goweii.ponyo.log

import android.os.Handler
import android.os.Looper
import android.os.Message
import per.goweii.ponyo.log.utils.ExecutorUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object Logcat {
    private const val MESSAGE_PUBLISH_LOG = 1001
    private const val MESSAGE_RESTART = 1002

    private var onCatchListener: OnCatchListener? = null
    private var catchHandler: CatchHandler = CatchHandler()
    private var catchRunnable: CatchRunnable? = null

    fun start() {
        val runnable = catchRunnable?.let {
            it.stop()
            it.copy()
        } ?: CatchRunnable(catchHandler)
        catchRunnable = runnable
        ExecutorUtils.execute(runnable)
    }

    fun stop() {
        catchRunnable?.stop()
    }

    fun registerListener(listener: OnCatchListener?) {
        onCatchListener = listener
    }

    private class CatchHandler : Handler(Looper.getMainLooper()) {
        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_PUBLISH_LOG -> onCatchListener?.onCatch(msg.obj as List<LogLine>)
                MESSAGE_RESTART -> start()
            }
        }
    }

    private class CatchRunnable(
        private val handler: CatchHandler
    ) : Runnable {
        private val myPid: Int = android.os.Process.myPid()
        private var isRunning = true
        private val cacheLines = LinkedList<LogLine>()
        private var lastLogLine: LogLine? = null
        private var lastPublishTime = 0L

        fun copy(): CatchRunnable {
            return CatchRunnable(handler).also {
                it.lastLogLine = lastLogLine
            }
        }

        private fun publishLogLines() {
            val message = Message.obtain()
            message.what = MESSAGE_PUBLISH_LOG
            message.obj = ArrayList(cacheLines)
            handler.sendMessage(message)
        }

        private fun sendRestart() {
            val message = Message.obtain()
            message.what = MESSAGE_RESTART
            handler.sendMessageDelayed(message, 100)
        }

        override fun run() {
            var process: Process? = null
            var reader: BufferedReader? = null
            try {
                val command = LogCommand()
                command.addBuffer(LogCommand.Buffer.MAIN)
                if (lastLogLine != null) {
                    val lastTimestamp = lastLogLine!!.timestamp
                    command.setSinceTime(lastTimestamp)
                }
                process = command.exec()
                val inputStream = process.inputStream
                reader = BufferedReader(InputStreamReader(inputStream), 8192)
                var line: String?
                while (reader.readLine().also { line = it } != null && isRunning) {
                    if (lastLogLine != null && lastLogLine!!.sameLogLine(line)) {
                        continue
                    }
                    val logLine = LogLine.newLogLine(line) ?: continue
                    lastLogLine = logLine
                    if (logLine.pid == myPid) {
                        cacheLines.add(logLine)
                    }
                    if (cacheLines.size > 100) {
                        publishLogLines()
                        cacheLines.clear()
                        lastPublishTime = System.nanoTime()
                    } else {
                        val currTime = System.nanoTime()
                        if (currTime - lastPublishTime > 100_000_000) {
                            publishLogLines()
                            cacheLines.clear()
                            lastPublishTime = currTime
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                process?.destroy()
                if (isRunning) {
                    sendRestart()
                }
            }
        }

        fun stop() {
            isRunning = false
        }
    }

    interface OnCatchListener {
        fun onCatch(logLines: List<LogLine>)
    }
}