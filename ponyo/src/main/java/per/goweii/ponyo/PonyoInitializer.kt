package per.goweii.ponyo

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup
internal class PonyoInitializer: Initializer {

    override fun priority(): Int = Initializer.PRIORITY_INITIAL

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            Ponyo.initialize(application)
        }
    }
}