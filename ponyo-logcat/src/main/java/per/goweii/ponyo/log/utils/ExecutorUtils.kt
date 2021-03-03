package per.goweii.ponyo.log.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ExecutorUtils {
    private val sExecutorService: ExecutorService by lazy {
        ThreadPoolExecutor(
            1, 1, 60L, TimeUnit.SECONDS,
            SynchronousQueue(),
            ThreadPoolExecutor.AbortPolicy()
        )
    }

    fun execute(r: Runnable) {
        sExecutorService.execute(r)
    }
}