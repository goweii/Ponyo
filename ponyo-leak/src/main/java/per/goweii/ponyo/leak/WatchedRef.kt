package per.goweii.ponyo.leak

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

internal class WatchedRef(
    val key: String,
    obj: Any,
    queue: ReferenceQueue<Any>
) : WeakReference<Any>(obj, queue) {
    val identity: String
        get() {
            val hex = Integer.toHexString(System.identityHashCode(get()))
            val name = get()?.javaClass?.simpleName ?: "null"
            return "$name@$hex"
        }

    var detectedTimes = 0
        private set

    val isRecycled: Boolean
        get() = get() == null

    val needReDetect: Boolean
        get() = detectedTimes < LeakConfig.maxDetectTimes

    var isReported: Boolean = false
        private set

    fun doneOnceDetected() {
        detectedTimes++
    }

    fun setReported() {
        isReported = true
    }
}