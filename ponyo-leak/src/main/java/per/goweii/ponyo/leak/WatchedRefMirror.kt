package per.goweii.ponyo.leak

import shark.HeapObject.HeapInstance
import shark.ValueHolder
import shark.ValueHolder.ReferenceHolder

internal class WatchedRefMirror(
    val referent: ReferenceHolder,
    val key: String,
    val description: String
) {
    val hasReferent = referent.value != ValueHolder.NULL_REFERENCE

    companion object {
        fun from(weakRef: HeapInstance): WatchedRefMirror {
            val weakRefClassName = weakRef.instanceClassName
            val keyString = weakRef[weakRefClassName, "key"]!!.value.readAsJavaString()!!
            val description = (weakRef[weakRefClassName, "description"]
                ?: weakRef[weakRefClassName, "name"])?.value?.readAsJavaString()
                ?: "Unknown (legacy)"
            return WatchedRefMirror(
                referent = weakRef["java.lang.ref.Reference", "referent"]!!.value.holder as ReferenceHolder,
                key = keyString,
                description = description
            )
        }
    }
}

