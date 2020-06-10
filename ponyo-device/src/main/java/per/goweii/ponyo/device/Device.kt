package per.goweii.ponyo.device

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.WindowManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Device {

    lateinit var application: Application
        internal set

    fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
    }

    override fun toString(): String {
        return """
            |唯一标识:$uniqueId
            |手机制造商:$manufacturer
            |手机品牌:$brand
            |设备型号:$model
            |设备名:$device
            |安卓ID:$androidId
            |屏幕尺寸:${size.run { "[$x,$y]" }}
            |系统版本:Android$sysVersionName($sysVersionCode)
            |运营商:$operatorName
            |应用包名:$packageName
            |应用版本名:$appVersionName
            |应用版本号:$appVersionCode
        """.trimMargin()
    }

    val uniqueId : String
        get() {
            return md5("$manufacturer-$brand-$model-$device-$androidId-$size")
        }

    val androidId: String
        get() {
            return Settings.System.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
        }

    val device: String
        get() {
            return Build.DEVICE
        }

    val manufacturer: String
        get() {
            return Build.MANUFACTURER
        }

    val brand: String
        get() {
            return Build.BRAND
        }

    val model: String
        get() {
            return Build.MODEL
        }

    val sysVersionName: String
        get() {
            return Build.VERSION.RELEASE
        }

    val sysVersionCode: Int
        get() {
            return Build.VERSION.SDK_INT
        }

    val operatorName: String
        get() {
            return telephonyManager.simOperatorName
        }

    val size: Point
        get() {
            return Point().apply { windowManager.defaultDisplay.getRealSize(this) }
        }

    val packageName: String
        get() {
            return application.packageName
        }

    val appVersionName: String
        get() {
            packageInfo.firstInstallTime
            return packageInfo.versionName
        }

    val appVersionCode: Long
        get() {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                packageInfo.versionCode.toLong()
            } else {
                packageInfo.longVersionCode
            }
        }

    private val packageInfo: PackageInfo
        get() {
            return application.packageManager.getPackageInfo(packageName, 0)!!
        }

    private val telephonyManager: TelephonyManager
        get() {
            return application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }

    private val windowManager: WindowManager
        get() {
            return application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }

    private fun md5(text: String): String {
        return try {
            val md5 = MessageDigest.getInstance("MD5")
            val digest = md5.digest(text.toByteArray())
            val sb = StringBuilder()
            for (b in digest) {
                val s = Integer.toHexString(b.toInt() and 0xff)
                if (s.length == 1) sb.append("0")
                sb.append(s)
            }
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            text.replace(" ", "")
        }
    }

}