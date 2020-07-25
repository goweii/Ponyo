package per.goweii.android.ponyo.startup

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@Startup
class DependTestInitializer: Initializer {
    override fun initialize(application: Application, isMainProcess: Boolean) {
    }

    override fun depends(): Set<Class<out Initializer>> {
        return mutableSetOf(
            TestInitializer::class.java
        )
    }
}