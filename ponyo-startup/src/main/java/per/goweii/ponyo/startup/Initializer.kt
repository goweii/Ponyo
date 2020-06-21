package per.goweii.ponyo.startup

import android.app.Application

/**
 * @author CuiZhen
 * @date 2020/6/21
 */
interface Initializer {
    fun initialize(application: Application)
    fun async(): Boolean
    fun priority(): Int
}