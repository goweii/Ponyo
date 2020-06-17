package per.goweii.ponyo.leak

import android.app.Application
import android.os.Handler
import android.os.Looper
import java.lang.ref.ReferenceQueue
import java.util.*

object LeakWatcher {
    private lateinit var activityWatcher: ActivityWatcher

    private val watchedObjects = mutableMapOf<String, WatchedRef>()
    private val watchedQueue = ReferenceQueue<Any>()

    private val mainHandler = Handler(Looper.getMainLooper())

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
        mainHandler.postDelayed({
            Looper.myQueue().addIdleHandler {
                GCExecutor.gc()
                removeRecycledObjects()
                if (watchedObjects.isNotEmpty()) {
                    //Leak

                }
                return@addIdleHandler false
            }
        }, 5000)
    }

}