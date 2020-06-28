package per.goweii.ponyo.startup.annotation

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Startup(
    val activities: Array<String> = []
)