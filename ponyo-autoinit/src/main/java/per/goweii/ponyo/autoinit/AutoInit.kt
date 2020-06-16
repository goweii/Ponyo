package per.goweii.ponyo.autoinit

import android.app.Application
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

/**
 * @author CuiZhen
 * @date 2020/6/6
 */
object AutoInit {

    private val onInitializes = mutableListOf<String>().apply {
        add("per.goweii.ponyo.Ponyo")
        add("per.goweii.ponyo.appstack.AppStack")
        add("per.goweii.ponyo.crash.Crash")
        add("per.goweii.ponyo.device.Device")
        add("per.goweii.ponyo.leak.Leak")
    }

    internal fun init(application: Application) {
        onInitializes.forEach {
            try {
                val cls = Class.forName(it).kotlin
                for (function in cls.functions) {
                    if (function.name == "initialize") {
                        if (function.parameters.size == 2) {
                            val instance = cls.objectInstance ?: cls.createInstance()
                            function.call(instance, application)
                            break
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

}