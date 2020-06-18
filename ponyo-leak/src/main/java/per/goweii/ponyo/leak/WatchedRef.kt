package per.goweii.ponyo.leak

import java.lang.ref.WeakReference

internal class WatchedRef (
        val key: String,
        obj: Any
) : WeakReference<Any>(obj) {
    val watchTime = System.nanoTime()

    val identity: String
        get() {
            val hex = Integer.toHexString(System.identityHashCode(get()))
            val name = get()?.javaClass?.simpleName ?: "null"
            return "$name@$hex"
        }
}