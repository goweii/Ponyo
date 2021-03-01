package per.goweii.ponyo.leak

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import java.util.*

class LeakInitializer: Initializer<Unit> {
     override fun create(context: Context) {
         Leak.initialize(context as Application)
     }

     override fun dependencies(): MutableList<Class<out Initializer<*>>> {
         return Collections.emptyList()
     }
 }