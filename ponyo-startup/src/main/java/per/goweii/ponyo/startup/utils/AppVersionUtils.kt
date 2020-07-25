package per.goweii.ponyo.startup.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build


/**
 * @author CuiZhen
 * @date 2020/7/25
 */
class AppVersionUtils {

    companion object {

        fun getVersion(context: Context): String {
            return "${getVersionName(context)}.${getVersionCode(context)}"
        }

        private fun getVersionName(context: Context): String {
            try {
                val packageManager: PackageManager = context.packageManager
                val packageInfo: PackageInfo = packageManager.getPackageInfo(context.packageName, 0)
                return packageInfo.versionName
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        private fun getVersionCode(context: Context): Long {
            try {
                val packageManager: PackageManager = context.packageManager
                val packageInfo: PackageInfo = packageManager.getPackageInfo(context.packageName, 0)
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    packageInfo.versionCode.toLong()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0L
        }
    }
}