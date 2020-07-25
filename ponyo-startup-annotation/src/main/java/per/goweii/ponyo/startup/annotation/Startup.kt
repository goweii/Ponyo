package per.goweii.ponyo.startup.annotation

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Startup(
    val async: Boolean = false,
    val priority: Int = PRIORITY_MIDDLE,
    val activities: Array<String> = [],
    val fragments: Array<String> = []
) {
    companion object {
        const val PRIORITY_INITIAL = 0
        const val PRIORITY_BASIC = 1000
        const val PRIORITY_MIDDLE = 2000
        const val PRIORITY_OUTER = 3000
        const val PRIORITY_LAST = 4000
    }
}