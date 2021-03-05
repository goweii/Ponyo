package per.goweii.ponyo

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import per.goweii.ponyo.appstack.AppStackInitializer
import per.goweii.ponyo.crash.CrashInitializer
import per.goweii.ponyo.device.DeviceInitializer
import per.goweii.ponyo.leak.LeakInitializer

class PonyoInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        Ponyo.initialize(context as Application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf<Class<out Initializer<*>>>().apply {
            add(AppStackInitializer::class.java)
            add(CrashInitializer::class.java)
            add(DeviceInitializer::class.java)
            add(LeakInitializer::class.java)
        }
    }
}