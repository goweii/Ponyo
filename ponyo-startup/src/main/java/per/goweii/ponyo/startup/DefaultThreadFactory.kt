package per.goweii.ponyo.startup

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class DefaultThreadFactory : ThreadFactory {
    private val threadNumber = AtomicInteger(1)
    private val group: ThreadGroup
    private val namePrefix: String
    override fun newThread(runnable: Runnable): Thread {
        val threadName = namePrefix + threadNumber.getAndIncrement()
        val thread = Thread(group, runnable, threadName, 0)
        if (thread.isDaemon) {
            thread.isDaemon = false
        }
        if (thread.priority != Thread.NORM_PRIORITY) {
            thread.priority = Thread.NORM_PRIORITY
        }
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, _ -> }
        return thread
    }

    companion object {
        private val poolNumber = AtomicInteger(1)
    }

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = "Ponyo-Startup task pool No." + poolNumber.getAndIncrement() + ", thread No."
    }
}