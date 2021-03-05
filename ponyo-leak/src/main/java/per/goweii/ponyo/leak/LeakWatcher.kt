package per.goweii.ponyo.leak

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import android.util.Log
import java.lang.ref.ReferenceQueue
import java.util.*

internal object LeakWatcher {
    private lateinit var activityWatcher: ActivityWatcher

    private val queue = ReferenceQueue<Any>()
    private val watchedObjects = mutableMapOf<String, WatchedRef>()
    private val leakedObjects: List<WatchedRef>
        get() {
            val list = arrayListOf<WatchedRef>()
            watchedObjects.values.forEach {
                if (!it.needReDetect && !it.isRecycled) {
                    list.add(it)
                }
            }
            return list
        }

    fun initialize(application: Application) {
        if (this::activityWatcher.isInitialized) return
        activityWatcher = ActivityWatcher()
        activityWatcher.watch(application)
    }

    fun watch(obj: Any) {
        removeWeeklyObjects()
        val key = UUID.randomUUID().toString()
        watchedObjects[key] = WatchedRef(key, obj, queue)
        checkWatchedObjects()
    }

    private fun removeWeeklyObjects() {
        var ref: WatchedRef? = null
        while (null != queue.poll()?.also { ref = it as WatchedRef }) {
            watchedObjects.remove(ref!!.key)
        }
    }

    private fun removeRecycledObjects() {
        val iterator = watchedObjects.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().value.isRecycled) {
                iterator.remove()
            }
        }
    }

    private fun checkWatchedObjects() {
        mainHandler.removeCallbacks(mainRunnable)
        mainHandler.postDelayed(mainRunnable, LeakConfig.perDetectDelay.toLong())
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val mainRunnable = Runnable {
        Looper.myQueue().removeIdleHandler(idleHandler)
        removeWeeklyObjects()
        if (watchedObjects.isNotEmpty()) {
            Looper.myQueue().addIdleHandler(idleHandler)
        }
    }

    private val idleHandler = MessageQueue.IdleHandler {
        GCTrigger.gc()
        removeRecycledObjects()
        var needReDetect = false
        var needReport = false
        watchedObjects.values.forEach {
            it.doneOnceDetected()
            if (it.needReDetect) {
                needReDetect = true
            } else if (!it.isReported) {
                needReport = true
            }
        }
        if (needReDetect) {
            checkWatchedObjects()
        }
        if (needReport) {
            reportLeaked()
        }
        false
    }

    private fun reportLeaked() {
        val leakedObjects = leakedObjects
        leakedObjects.forEach { it.setReported() }
        Leak.leakListener?.onLeak(leakedObjects.size)
        val sb = StringBuilder()
        sb.append("${leakedObjects.size} leaks:")
        leakedObjects.forEach { sb.append("\n- ").append(it.identity) }
        Log.w("Ponyo-Leak", sb.toString())
    }

}