package per.goweii.android.ponyo

import android.app.Application
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.startup.Initializer
import per.goweii.ponyo.startup.annotation.Startup
import java.io.File

@Startup
class AppInitializer: Initializer {

    override fun priority(): Int = Initializer.PRIORITY_MIDDLE

    override fun initialize(application: Application, isMainProcess: Boolean) {
        if (isMainProcess) {
            val dir = File(application.filesDir, "log")
            val cacheDir = File(dir, "cache")
            val logDir = File(dir, "log")
            Ponlog.openFilePrinter(
                cacheDir.absolutePath,
                logDir.absolutePath,
                "ponyo",
                "5d1b2aea3423b9bab60db5a0f12783e68f8851dd5bbfdf457e75d8d4844871fe22d14f94c1983f29f1e0f80b39371cd1f95ab5dcc799e274640e1ece9ba6c9c0"
            )
        }
    }
}