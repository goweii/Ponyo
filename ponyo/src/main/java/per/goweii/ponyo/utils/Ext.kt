package per.goweii.ponyo.utils

val Any?.objectName: String get() = "${this?.javaClass?.name}@${identityHexString}"

val Any?.objectSimpleName: String get() = "${this?.javaClass?.simpleName}@${identityHexString}"

val Any?.identityHashCode: Int get() = System.identityHashCode(this)

val Any?.identityHexString: String get() = Integer.toHexString(identityHashCode)