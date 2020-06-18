package per.goweii.ponyo.leak

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import per.goweii.ponyo.log.Ponlog
import java.lang.ref.ReferenceQueue
import java.util.*

internal object LeakWatcher {
    private lateinit var activityWatcher: ActivityWatcher

    private val watchedObjects = mutableMapOf<String, WatchedRef>()

    fun initialize(application: Application) {
        if (this::activityWatcher.isInitialized) return
        activityWatcher = ActivityWatcher()
        activityWatcher.watch(application)
    }

    fun watch(obj: Any) {
        removeRecycledObjects()
        val key = UUID.randomUUID().toString()
        watchedObjects[key] = WatchedRef(key, obj)
        checkWatchedObjects()
    }

    private fun removeRecycledObjects() {
        val iterator = watchedObjects.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value.get() == null) {
                iterator.remove()
            }
        }
    }

    private fun checkWatchedObjects() {
        mainHandler.removeCallbacks(mainRunnable)
        mainHandler.postDelayed(mainRunnable, 5000)
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val mainRunnable = Runnable {
        Looper.myQueue().removeIdleHandler(idleHandler)
        Looper.myQueue().addIdleHandler(idleHandler)
    }

    private val idleHandler = MessageQueue.IdleHandler {
        GCExecutor.gc()
        removeRecycledObjects()
        if (watchedObjects.isNotEmpty()) {
            Ponlog.w {
                val sb = StringBuilder()
                sb.append("Some memory leaks may have occurred, you can view the details on panel. And the leaked classes are as follows:")
                watchedObjects.forEach {
                    sb.append("\n- ").append(it.value.identity)
                }
                sb.toString()
            }
            Leak.leakListener?.onLeak()
        }
        false
    }

}