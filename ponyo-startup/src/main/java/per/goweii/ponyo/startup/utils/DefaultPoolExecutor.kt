package per.goweii.ponyo.startup.utils

import java.util.concurrent.*

class DefaultPoolExecutor private constructor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: BlockingQueue<Runnable>,
    threadFactory: ThreadFactory
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    workQueue,
    threadFactory,
    RejectedExecutionHandler { _, _ -> }) {
    override fun afterExecute(r: Runnable, t: Throwable?) {
        var e: Throwable? = t
        super.afterExecute(r, t)
        if (e == null && r is Future<*>) {
            try {
                (r as Future<*>).get()
            } catch (ce: CancellationException) {
                e = ce
            } catch (ee: ExecutionException) {
                e = ee.cause
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val INIT_THREAD_COUNT = CPU_COUNT + 1
        private val MAX_THREAD_COUNT =
            INIT_THREAD_COUNT
        private const val SURPLUS_THREAD_LIFE = 30L

        @Volatile
        private var instance: DefaultPoolExecutor? = null

        fun getInstance(): DefaultPoolExecutor {
            if (null == instance) {
                synchronized(DefaultPoolExecutor::class.java) {
                    if (null == instance) {
                        instance =
                            DefaultPoolExecutor(
                                INIT_THREAD_COUNT,
                                MAX_THREAD_COUNT,
                                SURPLUS_THREAD_LIFE,
                                TimeUnit.SECONDS,
                                ArrayBlockingQueue(64),
                                DefaultThreadFactory()
                            )
                    }
                }
            }
            return instance!!
        }
    }
}