package per.goweii.ponyo.startup

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

internal class StartupInitializer : ContentProvider() {

    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        val cls = Class.forName("per.goweii.ponyo.startup.StartupHolder")
        val constructor = cls.getConstructor()
        val startupHolder = constructor.newInstance()
        val initializers = cls.getField("initializers").get(startupHolder) as List<*>
        initializers.forEach {
            it as Initializer
            it.initialize(application)
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