package per.goweii.ponyo.leak

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import java.lang.ref.ReferenceQueue
import java.util.*

internal object LeakWatcher {
    private lateinit var activityWatcher: ActivityWatcher

    private val watchedObjects = mutableMapOf<String, WatchedRef>()
    private val watchedQueue = ReferenceQueue<Any>()

    fun initialize(application: Application) {
        if (this::activityWatcher.isInitialized) return
        activityWatcher.watch(application)
    }

    fun watch(obj: Any) {
        removeRecycledObjects()
        val key = UUID.randomUUID().toString()
        watchedObjects[key] = WatchedRef(key, obj, watchedQueue)
        checkWatchedObjects()
    }

    private fun removeRecycledObjects() {
        var ref: WatchedRef?
        do {
            ref = watchedQueue.poll() as WatchedRef?
            if (ref != null) {
                watchedObjects.remove(ref.key)
            }
        } while (ref != null)
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
            Leak.leakListener?.onLeak()
        }
        false
    }

}