package per.goweii.ponyo.appstack

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup
internal class AppStackInitializer: Initializer {

    override fun priority(): Int = Initializer.PRIORITY_BASIC

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            AppStack.initialize(application)
        }
    }
}