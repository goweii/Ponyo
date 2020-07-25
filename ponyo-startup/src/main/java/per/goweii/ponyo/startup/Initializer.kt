package per.goweii.ponyo.startup

import android.app.Application
import androidx.annotation.IntRange

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
interface Initializer {
    fun initialize(application: Application, isMainProcess: Boolean)

    fun depends(): Set<Class<out Initializer>> = emptySet()
}