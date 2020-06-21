package per.goweii.android.ponyo.startup

import android.app.Application
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@Startup
class StartupTest: Initializer {
    override fun initialize(application: Application) {
        Ponlog.d { application.toString() }
    }

    override fun async(): Boolean {
        return false
    }

    override fun priority(): Int {
        return 1
    }
}