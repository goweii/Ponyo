package per.goweii.ponyo.log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class CatchRunnable(
    private val handler: CatchHandler,
    private val pid: Int,
    private val buffers: Array<LogCommand.Buffer>
) : Runnable {
    private var isRunning = false
    private var isShutdown = false
    private val cacheLines = LinkedList<LogLine>()
    private var lastLogLine: LogLine? = null
    private var foundLastLogLine = false
    private var lastPublishTime = 0L
    private val delayPublishRunnable = Runnable {
        handler.publish(cacheLines)
        cacheLines.clear()
        lastPublishTime = System.currentTimeMillis()
    }

    fun copy(): CatchRunnable {
        return CatchRunnable(handler, pid, buffers)
            .also { it.lastLogLine = lastLogLine }
    }

    override fun run() {
        isRunning = true
        lastPublishTime = 0L
        var process: Process? = null
        var reader: BufferedReader? = null
        try {
            val command = LogCommand()
            command.setPid(pid)
            buffers.forEach { command.addBuffer(it) }
            lastLogLine?.let {
                foundLastLogLine = false
                val sinceTime = LogTimeUtils.plus1ms(it.timestamp)
                command.setSinceTime(sinceTime)
            }
            process = command.exec()
            val inputStream = process.inputStream
            reader = BufferedReader(InputStreamReader(inputStream), 8192)
            var line: String?
            while (!isShutdown) {
                reader.readLine().also { line = it } ?: break
                val logLine = LogLine.newLogLine(line) ?: continue
                if (logLine.pid != pid) continue
                lastLogLine = logLine
                cacheLines.add(logLine)
                val currTime = System.currentTimeMillis()
                if (currTime - lastPublishTime > 500) {
                    handler.removeCallbacks(delayPublishRunnable)
                    handler.publish(cacheLines)
                    cacheLines.clear()
                    lastPublishTime = currTime
                } else {
                    handler.postDelayed(delayPublishRunnable, 500)
                }
            }
        } catch (e: IOException) {
        } finally {
            if (cacheLines.isNotEmpty()) {
                handler.removeCallbacks(delayPublishRunnable)
                handler.publish(cacheLines)
                cacheLines.clear()
                lastPublishTime = System.currentTimeMillis()
            }
            isRunning = false
            try {
                reader?.close()
            } catch (e: IOException) {
            }
            process?.destroy()
            if (!isShutdown) {
                handler.restart()
            }
        }
    }

    fun shutdown() {
        isShutdown = true
    }

    fun isShutdown(): Boolean = isShutdown

    fun isRunning(): Boolean = isRunning
}