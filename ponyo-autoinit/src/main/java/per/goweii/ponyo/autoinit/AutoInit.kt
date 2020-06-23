package per.goweii.ponyo.autoinit

import android.app.Application
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

/**
 * @author CuiZhen
 * @date 2020/6/6
 */
@Startup
class AutoInit : Initializer{
    private val onInitializes = mutableListOf<String>().apply {
        add("per.goweii.ponyo.Ponyo")
        add("per.goweii.ponyo.appstack.AppStack")
        add("per.goweii.ponyo.crash.Crash")
        add("per.goweii.ponyo.device.Device")
        add("per.goweii.ponyo.leak.Leak")
    }

    override fun initialize(application: Application) {
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

    override fun async(): Boolean {
        return false
    }

    override fun priority(): Int {
        return 0
    }

}