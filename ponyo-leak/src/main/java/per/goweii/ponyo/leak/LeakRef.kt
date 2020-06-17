package per.goweii.ponyo.leak

import java.lang.ref.WeakReference

data class LeakRef<T>(
        private val ref: T,
        val tag: String
) : WeakReference<T>(ref) {
    val watchTime = System.nanoTime()
}