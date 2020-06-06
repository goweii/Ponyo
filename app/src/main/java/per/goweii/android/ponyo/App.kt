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
        val dir = File(filesDir, "log")
        val cacheDir = File(dir, "cache")
        val logDir = File(dir, "log")
        Ponlog.openFilePrinter(
            cacheDir.absolutePath,
            logDir.absolutePath,
            "ponyo",
            "5d1b2aea3423b9bab60db5a0f12783e68f8851dd5bbfdf457e75d8d4844871fe22d14f94c1983f29f1e0f80b39371cd1f95ab5dcc799e274640e1ece9ba6c9c0"
        )
        Ponlog.d("App") { "onCreate" }
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        Ponlog.closeFilePrinter()
    }
}