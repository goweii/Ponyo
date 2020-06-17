package per.goweii.ponyo.leak

internal object GCExecutor {

    fun gc() {
        Runtime.getRuntime().gc()
    }
}