package per.goweii.ponyo.log

object Logcat {
    private val catchHandler = CatchHandler { onCatchListener?.onCatch(it) }
    private var logcatExecutor: LogcatExecutor? = null
    private var catchRunnable: CatchRunnable? = null

    private var pid: Int = android.os.Process.myPid()
    private var buffers: Array<LogCommand.Buffer> = arrayOf(LogCommand.Buffer.MAIN)

    private var onCatchListener: OnCatchListener? = null

    fun start() {
        catchRunnable?.shutdown()
        val runnable = CatchRunnable(catchHandler, pid, buffers)
            .also { catchRunnable = it }
        val executor = if (logcatExecutor?.isShutdown == false) {
            logcatExecutor!!
        } else {
            LogcatExecutor().also { logcatExecutor = it }
        }
        executor.execute(runnable)
    }

    fun resume() {
        val executor = logcatExecutor ?: return
        val runnable = catchRunnable ?: return
        if (runnable.isRunning()) return
        runnable.shutdown()
        catchRunnable = runnable.copy().also {
            executor.execute(it)
        }
    }

    fun pause() {
        catchRunnable?.shutdown()
    }

    fun stop() {
        catchRunnable?.shutdown()
        catchRunnable = null
        logcatExecutor?.shutdownNow()
        logcatExecutor = null
    }

    fun setPid(pid: Int) {
        this.pid = pid
    }

    fun getPid(): Int = this.pid

    fun setBuffer(buffers: Array<LogCommand.Buffer>) {
        this.buffers = buffers
    }

    fun getBuffers(): Array<LogCommand.Buffer> = this.buffers

    fun registerCatchListener(listener: OnCatchListener?) {
        onCatchListener = listener
    }

    interface OnCatchListener {
        fun onCatch(logLines: List<LogLine>)
    }
}