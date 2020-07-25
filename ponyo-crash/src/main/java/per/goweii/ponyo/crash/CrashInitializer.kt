package per.goweii.ponyo.crash

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup(priority = Startup.PRIORITY_BASIC)
internal class CrashInitializer: Initializer {

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            Crash.initialize(application)
        }
    }
}