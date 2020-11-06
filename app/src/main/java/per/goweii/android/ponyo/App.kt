package per.goweii.android.ponyo

import android.app.Application
import android.content.Context

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
//        Ponlog.d("App") { "attachBaseContext" }
        super.attachBaseContext(base)
    }

    override fun onCreate() {
//        Ponlog.d("App") { "onCreate" }
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
//        Ponlog.closeFilePrinter()
    }
}