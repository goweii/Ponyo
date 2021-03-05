package per.goweii.ponyo.crash

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import java.util.*

class CrashInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Crash.initialize(context as Application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}