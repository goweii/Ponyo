package per.goweii.ponyo.leak

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup(priority = Startup.PRIORITY_BASIC)
internal class LeakInitializer: Initializer {

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            Leak.initialize(application)
        }
    }
}