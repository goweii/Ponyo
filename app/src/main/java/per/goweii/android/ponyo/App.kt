package per.goweii.android.ponyo

import android.app.Application
import per.goweii.ponyo.log.Ponlog

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class App : Application() {

    override fun onCreate() {
        Ponlog.d("App") { "onCreate" }
        super.onCreate()
    }
}