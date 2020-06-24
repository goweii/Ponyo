package per.goweii.ponyo.startup

import android.app.Application
import android.content.Context
import android.os.Build

class InitializerPreferences(
    private val application: Application
) {

    companion object {
        private const val SP_NAME = "ponyo_startup"
        private const val KEY_INITIALIZERS = "initializers"
        private const val KEY_LAST_VERSION_CODE = "last_version_code"
        private const val KEY_LAST_VERSION_NAME = "last_version_name"
    }

    private val sp = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun getInitializers(): Set<String>? {
        val lastVersionCode = sp.getLong(KEY_LAST_VERSION_CODE, 0)
        val currVersionCode = currVersionCode()
        if (lastVersionCode != currVersionCode) {
            return null
        }
        val lastVersionName = sp.getString(KEY_LAST_VERSION_NAME, "")
        val currVersionName = currVersionName()
        if (lastVersionName != currVersionName) {
            return null
        }
        return sp.getStringSet(KEY_INITIALIZERS, null)
    }

    fun setInitializers(initializers: Set<String>) {
        sp.edit()
            .putLong(KEY_LAST_VERSION_CODE, currVersionCode())
            .putString(KEY_LAST_VERSION_NAME, currVersionName())
            .putStringSet(KEY_INITIALIZERS, initializers)
            .apply()
    }

    private fun currVersionCode(): Long {
        val pm = application.packageManager
        val pi = pm.getPackageInfo(application.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.longVersionCode
        } else {
            pi.versionCode.toLong()
        }
    }

    private fun currVersionName(): String {
        val pm = application.packageManager
        val pi = pm.getPackageInfo(application.packageName, 0)
        return pi.versionName
    }

}