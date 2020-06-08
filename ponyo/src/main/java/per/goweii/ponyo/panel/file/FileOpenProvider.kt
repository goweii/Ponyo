package per.goweii.ponyo.panel.file

import android.content.Context
import androidx.core.content.FileProvider

class FileOpenProvider: FileProvider() {
    companion object {
        fun authorities(context: Context): String {
            return "${context.packageName}.panel.file.fileopenprovider"
        }
    }
}