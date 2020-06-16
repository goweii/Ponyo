package per.goweii.ponyo.leak

import java.lang.ref.WeakReference

data class LeakInfo(
    val objTag: String,
    val objRef: WeakReference<Any>
)