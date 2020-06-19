package per.goweii.ponyo.leak

import android.app.Application
import android.os.Debug
import kotlinx.coroutines.*
import per.goweii.ponyo.log.Ponlog
import shark.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal object HprofDumper : CoroutineScope by MainScope() {

    fun dump(application: Application, callback: (hprofFile: File) -> Unit) {
        launch {
            val hprofFile = withContext(Dispatchers.IO) {
                val dir = File(application.filesDir, "leak")
                dir.mkdirs()
                dir.listFiles()?.forEach { it.delete() }
                val fileName =
                    SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS'.hprof'", Locale.CHINA).format(Date())
                File(dir, fileName)
            }
            callback.invoke(hprofFile)
        }
    }

}