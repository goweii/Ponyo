package per.goweii.ponyo.panel.file

import android.content.Context
import kotlinx.coroutines.*
import java.io.File
import kotlin.text.StringBuilder

object FileManager : CoroutineScope by MainScope() {

    data class FileEntity(
        val name: String,
        val path: String,
        val isDir: Boolean
    )

    fun getRootDataDir(context: Context): FileEntity {
        val filesDir = context.filesDir
        val dataDir = File(filesDir.parent!!)
        return FileEntity(dataDir.name, dataDir.absolutePath, dataDir.isDirectory)
    }

    fun childFilesOrNull(fileEntity: FileEntity): List<FileEntity>? {
        val file = File(fileEntity.path)
        if (!file.isDirectory) {
            return null
        }
        val list = arrayListOf<FileEntity>()
        file.listFiles()?.forEach {
            list.add(FileEntity(it.name, it.absolutePath, it.isDirectory))
        }
        return list
    }

    private var readStrFilePath: String? = null
    private var readStrFileDeferred: Deferred<String>? = null

    fun closeStrFile() {
        readStrFilePath = null
        readStrFileDeferred?.cancel()
        readStrFileDeferred = null
    }

    fun readStrFile(fileEntity: FileEntity, callback: (str: String) -> Unit) {
        launch {
            val file = File(fileEntity.path)
            if (file.isDirectory) {
                callback("")
                return@launch
            }
            if (readStrFilePath == fileEntity.path) {
                return@launch
            }
            readStrFileDeferred?.cancel()
            readStrFileDeferred = async(Dispatchers.IO) {
                try {
                    file.readText()
                } catch (e: Throwable) {
                    "read file error"
                }
            }
            callback(readStrFileDeferred!!.await())
            readStrFilePath = null
            readStrFileDeferred = null
        }
    }

}