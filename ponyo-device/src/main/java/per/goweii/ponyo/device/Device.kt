package per.goweii.ponyo.device

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import androidx.annotation.RequiresPermission

object Device {

    lateinit var application: Application
        internal set

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
    }

    /**
     * 获取手机厂商
     */
    val brand: String
        get() {
            return Build.BRAND
        }

    /**
     * 获取设备型号
     */
    val model: String
        get() {
            return Build.MODEL
        }

    /**
     * 获取当前系统版本
     */
    val version: String
        get() {
            return Build.VERSION.RELEASE
        }

    /**
     * 获取当前系统版本
     */
    val operatorName: String
        get() {
            val tm = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.simOperatorName
        }

    /**
     * 获取联网方式
     */
    val networkState: String
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        get() {
            var strNetworkType = "Unknown"
            val connManager =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isAvailable) {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI"
                } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    val _strSubTypeName = networkInfo.subtypeName
                    val networkType = networkInfo.subtype
                    strNetworkType = when (networkType) {
                        TelephonyManager.NETWORK_TYPE_GPRS,
                        TelephonyManager.NETWORK_TYPE_EDGE,
                        TelephonyManager.NETWORK_TYPE_CDMA,
                        TelephonyManager.NETWORK_TYPE_1xRTT,
                        TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                        TelephonyManager.NETWORK_TYPE_UMTS,
                        TelephonyManager.NETWORK_TYPE_EVDO_0,
                        TelephonyManager.NETWORK_TYPE_EVDO_A,
                        TelephonyManager.NETWORK_TYPE_HSDPA,
                        TelephonyManager.NETWORK_TYPE_HSUPA,
                        TelephonyManager.NETWORK_TYPE_HSPA,
                        TelephonyManager.NETWORK_TYPE_EVDO_B,
                        TelephonyManager.NETWORK_TYPE_EHRPD,
                        TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                        TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                        else -> {
                            if (_strSubTypeName.equals("TD-SCDMA", true) ||
                                _strSubTypeName.equals("WCDMA", true) ||
                                _strSubTypeName.equals("CDMA2000", true)
                            ) {
                                "3G"
                            } else {
                                _strSubTypeName
                            }
                        }
                    }
                }
            }
            return strNetworkType
        }

    val displayMetrics: DisplayMetrics
        get() {
            return Resources.getSystem().displayMetrics
        }

    val widthPixels: Int
        get() {
            return displayMetrics.widthPixels
        }

    val heightPixels: Int
        get() {
            return displayMetrics.heightPixels
        }

    val packageName: String
        get() {
            return application.packageName
        }

    val packageInfo: PackageInfo
        get() {
            return application.packageManager.getPackageInfo(packageName, 0)!!
        }

    val versionName: String
        get() {
            return packageInfo.versionName
        }

    val versionCode: Long
        get() {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                packageInfo.versionCode.toLong()
            } else {
                packageInfo.longVersionCode
            }
        }

}