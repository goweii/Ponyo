package per.goweii.ponyo.panel.sp

import android.content.Context
import java.io.File
import kotlin.text.StringBuilder

object SpManager {

    data class Sp(
        val key: String,
        val value: String
    )

    val spNames by lazy { mutableListOf<String>() }

    fun findAllSp(context: Context) {
        val filesDir = context.filesDir
        val spDir = File(filesDir.parent, "shared_prefs")
        val spFiles = spDir.listFiles()
        spFiles?.forEach {
            val fileName = it.name
            spNames.add(fileName.substring(0, fileName.length - 4))
        }
    }

    fun readSp(context: Context, spName: String): List<Sp> {
        val list = arrayListOf<Sp>()
        val sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE)
        sp.all.forEach {
            val key = it.key
            val value = it.value
            if (value is Set<*>) {
                val sb = StringBuilder()
                value.forEachIndexed { index, any ->
                    if (index == 0) {
                        sb.append(any.toString())
                    } else {
                        sb.append("\n").append(any.toString());
                    }
                }
                list.add(Sp(key, sb.toString()))
            } else {
                list.add(Sp(key, value.toString()))
            }
        }
        return list
    }

}