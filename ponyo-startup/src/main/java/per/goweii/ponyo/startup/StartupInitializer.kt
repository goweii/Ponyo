package per.goweii.ponyo.startup

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import per.goweii.ponyo.startup.annotation.Const

internal class StartupInitializer : ContentProvider() {

    override fun onCreate(): Boolean = try {
        val application = context!!.applicationContext as Application
        val holders = ClassUtils.getFileNameByPackageName(application, Const.GENERATED_PACKAGE_NAME)
        holders.forEach { holderName ->
            val cls = Class.forName(holderName)
            val constructor = cls.getConstructor()
            val startupHolder = constructor.newInstance()
            val inits = cls.getField(Const.GENERATED_LIST_FIELD).get(startupHolder) as List<*>
            inits.forEach { initName ->
                initName as String
                val insCls = Class.forName(initName)
                val initializer = insCls.newInstance()
                initializer as Initializer
                initializer.initialize(application)
            }
        }
        true
    } catch (e: Exception) {
        false
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