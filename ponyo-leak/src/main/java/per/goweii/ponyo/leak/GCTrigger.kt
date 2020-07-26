package per.goweii.ponyo.leak

internal object GCTrigger {

    fun gc() {
        Runtime.getRuntime().gc()
        Runtime.getRuntime().runFinalization()
    }
}