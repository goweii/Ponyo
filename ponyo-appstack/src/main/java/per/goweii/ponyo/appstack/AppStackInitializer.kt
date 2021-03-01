package per.goweii.ponyo.appstack

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import java.util.*

class AppStackInitializer: Initializer<Unit> {
     override fun create(context: Context) {
         AppStack.initialize(context as Application)
     }

     override fun dependencies(): MutableList<Class<out Initializer<*>>> {
         return Collections.emptyList()
     }
 }