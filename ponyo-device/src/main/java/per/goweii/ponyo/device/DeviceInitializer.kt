package per.goweii.ponyo.device

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup

@Startup(priority = Startup.PRIORITY_BASIC)
internal class DeviceInitializer: Initializer {

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            Device.initialize(application)
        }
    }
}