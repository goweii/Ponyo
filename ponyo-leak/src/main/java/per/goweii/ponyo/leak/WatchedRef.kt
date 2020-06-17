package per.goweii.ponyo.leak

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class WatchedRef (
        val key: String,
        obj: Any,
        queue: ReferenceQueue<Any>
) : WeakReference<Any>(obj, queue) {
    val watchTime = System.nanoTime()
}