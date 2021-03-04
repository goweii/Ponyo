package per.goweii.ponyo.log

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class LogcatExecutor : ThreadPoolExecutor(
    1,
    1,
    60L,
    TimeUnit.SECONDS,
    SynchronousQueue(),
    DiscardPolicy()
)