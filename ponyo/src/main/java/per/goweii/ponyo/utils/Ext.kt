package per.goweii.ponyo.utils

import android.content.Context

val Any?.objectName: String get() = "${this?.javaClass?.name}@${identityHexString}"

val Any?.objectSimpleName: String get() = "${this?.javaClass?.simpleName}@${identityHexString}"

val Any?.identityHashCode: Int get() = System.identityHashCode(this)

val Any?.identityHexString: String get() = Integer.toHexString(identityHashCode)

val Context.statusBarHeight: Int
    get() {
        return try {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            resources.getDimensionPixelSize(resourceId)
        } catch (e: Exception) {
            0
        }
    }