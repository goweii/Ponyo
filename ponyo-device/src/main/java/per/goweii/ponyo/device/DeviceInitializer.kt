package per.goweii.ponyo.device

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import java.util.*

class DeviceInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        Device.initialize(context as Application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}