package per.goweii.ponyo.leak

import android.app.Application
import android.os.Debug
import kotlinx.coroutines.*
import shark.Hprof
import shark.HprofHeapGraph
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

internal class HprofDumper(
    private val application: Application
) : CoroutineScope by MainScope() {

    fun dump() {
        launch {
            val dir = File(application.filesDir, "leak")
            dir.mkdirs()
            dir.listFiles()?.forEach { it.delete() }
            val fileName = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS'.hprof'", Locale.CHINA).format(Date())
            val hprofFile = File(dir, fileName)
            withContext(Dispatchers.IO) {
                Debug.dumpHprofData(hprofFile.absolutePath)
            }
            withContext(Dispatchers.IO) {
                Hprof.open(hprofFile).use { hprof ->
                    val heapGraph = HprofHeapGraph.indexHprof(hprof)
                    val threadClass = heapGraph.findClassByName("java.lang.Thread")!!
                    val threadNames: Sequence<String> = threadClass.instances.map { instance ->
                        val nameField = instance["java.lang.Thread", "name"]!!
                        nameField.value.readAsJavaString()!!
                    }
                    threadNames.forEach { println(it) }
                }
            }
        }
    }

}