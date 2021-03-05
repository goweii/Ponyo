package per.goweii.ponyo.panel.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object FileManager : CoroutineScope by MainScope() {
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

    fun readStrFile(file: File, callback: (str: String) -> Unit) {
        launch {
            closeStrFile()
            readerClosed = false
            readStrFileDeferred = async(Dispatchers.IO) {
                try {
                    val br = BufferedReader(FileReader(file.absolutePath)).also { reader = it }
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

    fun openFile(context: Context, file: File) {
        try {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
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
            e.printStackTrace()
        }
    }

    fun deleteFile(file: File): Boolean {
        fun deleteFile(file: File): Boolean {
            if (!file.exists()) return true
            if (!file.isFile) return false
            return file.delete()
        }
        fun deleteDir(file: File): Boolean {
            if (!file.exists()) return true
            if (!file.isDirectory) return false
            file.listFiles().forEach {
                if (it.isFile) {
                    if (!deleteFile(it)) return false
                } else if (it.isDirectory) {
                    if (!deleteDir(it)) return false
                }
            }
            return file.delete()
        }
        return if (file.isDirectory) {
            deleteDir(file)
        } else {
            deleteFile(file)
        }
    }

    private fun getMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "text/plain"
    }

}