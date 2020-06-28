package per.goweii.android.ponyo.startup

import android.app.Application
import per.goweii.android.ponyo.log.LogActivity
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
@Startup()
class TestInitializer: Initializer {

    override fun priority(): Int {
        return Initializer.PRIORITY_LAST
    }

    override fun initialize(application: Application, isMainProcess: Boolean) {
        Ponlog.d { application.toString() }
    }
}