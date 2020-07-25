package per.goweii.ponyo.leak

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup
internal class LeakInitializer: Initializer {

    override fun priority(): Int = Initializer.PRIORITY_BASIC

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            Leak.initialize(application)
        }
    }
}