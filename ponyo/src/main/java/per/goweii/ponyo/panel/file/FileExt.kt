package per.goweii.ponyo.panel.file

import android.content.Context
import android.text.TextUtils
import android.text.format.Formatter
import java.io.File

fun Context.rootAppDir(): File {
    val filesDir = cacheDir
    return File(filesDir.parent!!)
}

fun Context.rootAppExternalDir(): File? {
    val filesDir = externalCacheDir ?: return null
    return File(filesDir.parent)
}

fun File.isRootAppDir(context: Context): Boolean {
    return TextUtils.equals(absolutePath, context.rootAppDir().absolutePath)
}

fun File.isRootExternalAppDir(context: Context): Boolean {
    val rootAppExternalDir = context.rootAppExternalDir() ?: return false
    return TextUtils.equals(absolutePath, rootAppExternalDir.absolutePath)
}

fun File.name(context: Context): String {
    return when {
        isRootAppDir(context) -> "内部存储"
        isRootExternalAppDir(context) -> "外部存储"
        else -> name
    }
}

fun File.fullLengthFormatted(context: Context): String {
    return Formatter.formatFileSize(context, fullLength())
}

fun File.fullLength(): Long {
    if (!isDirectory) {
        return length()
    }
    var size: Long = 0
    listFiles()?.forEach {
        size += it.fullLength()
    }
    return size
}

fun File.childFiles(): List<File>? {
    if (!isDirectory) {
        return null
    }
    return listFiles().toList()
}