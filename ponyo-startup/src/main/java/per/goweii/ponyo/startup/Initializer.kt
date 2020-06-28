package per.goweii.ponyo.startup

import android.app.Application
import androidx.annotation.IntRange

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
interface Initializer {
    fun initialize(application: Application, isMainProcess: Boolean)

    fun async(): Boolean = false

    @IntRange(from = PRIORITY_INITIAL.toLong(), to = PRIORITY_LAST.toLong())
    fun priority(): Int = PRIORITY_MIDDLE

    fun depends(): Set<Class<out Initializer>> = emptySet()

    companion object {
        const val PRIORITY_INITIAL = 0
        const val PRIORITY_BASIC = 1000
        const val PRIORITY_MIDDLE = 2000
        const val PRIORITY_OUTER = 3000
        const val PRIORITY_LAST = 4000
    }
}