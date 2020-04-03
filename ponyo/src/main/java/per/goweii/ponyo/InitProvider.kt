package per.goweii.ponyo

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.google.gson.GsonBuilder
import per.goweii.ponyo.appstack.AppLifecycle
import per.goweii.ponyo.log.JsonFormatter
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.panel.log.LogManager

class InitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val context = context
        if (context is Application) {
            Ponyo.onInitialize(context)
        }
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}