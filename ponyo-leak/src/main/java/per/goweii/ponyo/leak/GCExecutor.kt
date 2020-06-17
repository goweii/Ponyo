package per.goweii.ponyo.leak

internal class GCExecutor {

    fun gc() {
        Runtime.getRuntime().gc()
    }
}