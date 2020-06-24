package per.goweii.ponyo.crash

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup
internal class CrashInitializer: Initializer {

    override fun priority(): Int = Initializer.PRIORITY_INITIAL

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            Crash.initialize(application)
        }
    }
}