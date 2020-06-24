package per.goweii.android.ponyo

import android.app.Application
import per.goweii.ponyo.crash.Crash
import per.goweii.ponyo.log.Ponlog
import java.io.File

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class App : Application() {

    override fun onCreate() {
        Ponlog.d("App") { "onCreate" }
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        Ponlog.closeFilePrinter()
    }
}