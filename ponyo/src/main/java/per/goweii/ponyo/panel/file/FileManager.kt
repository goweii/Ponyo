package per.goweii.ponyo.panel.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.format.Formatter
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object FileManager : CoroutineScope by MainScope() {

    data class FileEntity(
        var name: String,
        val path: String,
        val isDir: Boolean,
        val length: Long
    ) {
        fun formatLength(context: Context): String {
            return Formatter.formatFileSize(context, length)
        }
    }

    private fun File.size(): Long {
        if (!isDirectory) {
            return length()
        }
        var size: Long = 0
        listFiles()?.forEach {
            size += it.size()
        }
        return size
    }

    fun getRootDataDir(context: Context): FileEntity {
        val filesDir = context.cacheDir
        val dataDir = File(filesDir.parent!!)
        return FileEntity(dataDir.name, dataDir.absolutePath, dataDir.isDirectory, dataDir.size())
    }

    fun getRootExternalDir(context: Context): FileEntity {
        val filesDir = context.externalCacheDir
        val dataDir = File(filesDir?.parent ?: "")
        return FileEntity(dataDir.name, dataDir.absolutePath, dataDir.isDirectory, dataDir.size())
    }

    fun childFilesOrNull(fileEntity: FileEntity): List<FileEntity>? {
        val file = File(fileEntity.path)
        if (!file.isDirectory) {
            return null
        }
        val list = arrayListOf<FileEntity>()
        file.listFiles()?.forEach {
            list.add(FileEntity(it.name, it.absolutePath, it.isDirectory, it.size()))
        }
        return list
    }

    private var readStrFileDeferred: Deferred<String>? = null
    private var reader: BufferedReader? = null
    private var readerClosed = false

    fun closeStrFile() {
        readerClosed = true
        try {
            reader?.close()
        } catch (e: Exception) {
        }
        readStrFileDeferred?.cancel()
        readStrFileDeferred = null
    }

    fun readStrFile(fileEntity: FileEntity, callback: (str: String) -> Unit) {
        launch {
            closeStrFile()
            readerClosed = false
            readStrFileDeferred = async(Dispatchers.IO) {
                try {
                    val br = BufferedReader(FileReader(fileEntity.path)).also { reader = it }
                    val sb = StringBuilder()
                    var s: String?
                    var lines = 0
                    while (null != br.readLine().also { s = it }) {
                        if (readerClosed) break
                        sb.append(s)
                        lines++
                        if (lines >= 100) break
                    }
                    val ss = sb.toString()
                    ss
                } catch (e: Exception) {
                    ""
                } finally {
                    try {
                        reader?.close()
                    } catch (e: Exception) {
                    }
                }
            }
            callback(readStrFileDeferred!!.await())
            readStrFileDeferred = null
        }
    }

    fun openFile(context: Context, fileEntity: FileEntity) {
        try {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
            val file = File(fileEntity.path)
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, FileOpenProvider.authorities(context), file)
            } else {
                Uri.fromFile(file)
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, getMimeType(context, uri))
            context.startActivity(intent)
            Intent.createChooser(intent, "请选择对应的软件打开该文件")
        } catch (e: Exception) {
        }
    }

    private fun getMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "text/plain"
    }

}