package per.goweii.ponyo.leak

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

internal class WatchedRef (
        val key: String,
        obj: Any,
        queue: ReferenceQueue<Any>
) : WeakReference<Any>(obj, queue) {
    val watchTime = System.nanoTime()

    val identity: String
        get() {
            val hex = Integer.toHexString(System.identityHashCode(get()))
            val name = get()?.javaClass?.simpleName ?: "null"
            return "$name@$hex"
        }
}