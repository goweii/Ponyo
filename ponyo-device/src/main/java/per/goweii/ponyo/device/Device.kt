package per.goweii.ponyo.device

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.WindowManager
import androidx.annotation.RequiresPermission
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
        val sb = StringBuilder()
        var i = 0
        toMap().forEach {
            if (i != 0) sb.append("\n")
            sb.append(it.key).append(":").append(it.value)
            i++
        }
        return sb.toString()
    }

    fun toMap(): HashMap<String, String> {
        return LinkedHashMap<String, String>().apply {
            put("应用包名", packageName)
            put("应用版本名", appVersionName)
            put("应用版本号", "$appVersionCode")
            put("唯一标识", uniqueId)
            put("安卓ID", androidId)
            put("手机制造商", manufacturer)
            put("手机品牌", brand)
            put("设备型号", model)
            put("设备名", device)
            put("屏幕尺寸", "${screenInch}英寸")
            put("屏幕分辨率", Device.size.run { "[$x,$y]" })
            put("系统版本", "Android$sysVersionName($sysVersionCode)")
            put("CPU指令集", supportedAbis.contentToString())
            put("运营商", operatorName)
            put("WIFI状态", wifiStatus)
            put("WIFI名", wifiName)
            put("WIFI IP", wifiIP)
            put("SD卡空间", sdCardSpace)
            put("RAM空间", ramSpace)
            put("是否ROOT", "$isRoot")
        }
    }

    val uniqueId: String
        get() = md5("$manufacturer-$brand-$model-$device-$androidId-$size")

    val androidId: String
        get() = Settings.System.getString(
            application.contentResolver,
            Settings.Secure.ANDROID_ID
        )

    val device: String = Build.DEVICE

    val manufacturer: String = Build.MANUFACTURER

    val brand: String = Build.BRAND

    val model: String = Build.MODEL

    val sysVersionName: String = Build.VERSION.RELEASE

    val sysVersionCode: Int = Build.VERSION.SDK_INT

    val operatorName: String get() = telephonyManager.simOperatorName

    val size: Point get() = Point().apply { windowManager.defaultDisplay.getRealSize(this) }

    val packageName: String get() = application.packageName

    val appVersionName: String get() = packageInfo.versionName

    val appVersionCode: Long
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            packageInfo.versionCode.toLong()
        } else {
            packageInfo.longVersionCode
        }

    val supportedAbis: Array<String> = Build.SUPPORTED_ABIS
    val supportedAbis32: Array<String> = Build.SUPPORTED_32_BIT_ABIS
    val supportedAbis64: Array<String> = Build.SUPPORTED_64_BIT_ABIS

    val isRoot: Boolean get() = DeviceUtils.isDeviceRooted()

    val wifiStatus: String
        @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
        get() = DeviceUtils.getWifiState(application)

    val isWifiConnect: Boolean
        @RequiresPermission("android.permission.ACCESS_NETWORK_STATE")
        get() = DeviceUtils.isWifiConnect(application)

    val wifiName: String
        get() = DeviceUtils.getWifiName(application)

    val wifiIP: String
        get() = DeviceUtils.getWifiIP(application)

    val sdCardSpace: String
        get() = DeviceUtils.getSDCardSpace(application)

    val sdCardTotalSize: String
        get() = DeviceUtils.getSDTotalSize(application)

    val sdCardAvailableSize: String
        get() = DeviceUtils.getSDAvailableSize(application)

    val ramSpace: String
        get() = DeviceUtils.getMemorySpace(application)

    val ramTotalSize: String
        get() = DeviceUtils.getMemoryTotalSize(application)

    val ramAvailableSize: String
        get() = DeviceUtils.getMemoryAvailSize(application)

    val screenInch: Double
        get() = DeviceUtils.getScreenInch(application)

    private val packageInfo: PackageInfo
        get() = application.packageManager.getPackageInfo(
            packageName,
            0
        )!!

    private val telephonyManager: TelephonyManager
        get() = application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val windowManager: WindowManager
        get() = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

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