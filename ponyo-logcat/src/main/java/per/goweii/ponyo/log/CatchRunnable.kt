package per.goweii.ponyo.log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.LinkedList

class CatchRunnable(
    private val handler: CatchHandler,
    private val pid: Int,
    private val buffers: Array<LogCommand.Buffer>
) : Runnable {
    private var isRunning = true
    private val cacheLines = LinkedList<LogLine>()
    private var lastLogLine: LogLine? = null
    private var lastPublishTime = 0L

    fun copy(): CatchRunnable {
        return CatchRunnable(handler, pid, buffers)
            .also { it.lastLogLine = lastLogLine }
    }

    override fun run() {
        var process: Process? = null
        var reader: BufferedReader? = null
        try {
            val command = LogCommand()
            command.setPid(pid)
            buffers.forEach { command.addBuffer(it) }
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
                if (logLine.pid == pid) {
                    cacheLines.add(logLine)
                }
                if (cacheLines.size > 100) {
                    handler.publish(cacheLines)
                    cacheLines.clear()
                    lastPublishTime = System.nanoTime()
                } else {
                    val currTime = System.nanoTime()
                    if (currTime - lastPublishTime > 100_000_000) {
                        handler.publish(cacheLines)
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
                handler.restart()
            }
        }
    }

    fun stop() {
        isRunning = false
    }
}