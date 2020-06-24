package per.goweii.ponyo.startup

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import dalvik.system.DexFile
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.regex.Pattern

object ClassFinder {
    private const val EXTRACTED_NAME_EXT = ".classes"
    private const val EXTRACTED_SUFFIX = ".zip"
    private val SECONDARY_FOLDER_NAME = "code_cache" + File.separator + "secondary-dexes"
    private const val PREFS_FILE = "multidex.version"
    private const val KEY_DEX_NUMBER = "dex.number"
    private const val VM_WITH_MULTIDEX_VERSION_MAJOR = 2
    private const val VM_WITH_MULTIDEX_VERSION_MINOR = 1
    private fun getMultiDexPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            PREFS_FILE, Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS
        )
    }

    @Throws(
        PackageManager.NameNotFoundException::class,
        IOException::class,
        InterruptedException::class
    )
    fun findAllClassByPackageName(
        context: Context,
        packageName: String?
    ): Set<String> {
        val classNames: MutableSet<String> = HashSet()
        val paths = getSourcePaths(context)
        val parserCtl = CountDownLatch(paths.size)
        for (path in paths) {
            DefaultPoolExecutor.getInstance().execute {
                var dexfile: DexFile? = null
                try {
                    dexfile = if (path.endsWith(EXTRACTED_SUFFIX)) {
                        DexFile.loadDex(path, "$path.tmp", 0)
                    } else {
                        DexFile(path)
                    }
                    val dexEntries = dexfile!!.entries()
                    while (dexEntries.hasMoreElements()) {
                        val className = dexEntries.nextElement()
                        if (className.startsWith(packageName!!)) {
                            classNames.add(className)
                        }
                    }
                } catch (ignore: Throwable) {
                } finally {
                    if (null != dexfile) {
                        try {
                            dexfile.close()
                        } catch (ignore: Throwable) {
                        }
                    }
                    parserCtl.countDown()
                }
            }
        }
        parserCtl.await()
        return classNames
    }

    @Throws(PackageManager.NameNotFoundException::class, IOException::class)
    private fun getSourcePaths(context: Context): List<String> {
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val sourceApk = File(applicationInfo.sourceDir)
        val sourcePaths: MutableList<String> = ArrayList()
        sourcePaths.add(applicationInfo.sourceDir)
        val extractedFilePrefix = sourceApk.name + EXTRACTED_NAME_EXT
        if (!isVMMultidexCapable) {
            val totalDexNumber = getMultiDexPreferences(context).getInt(KEY_DEX_NUMBER, 1)
            val dexDir = File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME)
            for (secondaryNumber in 2..totalDexNumber) {
                val fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX
                val extractedFile = File(dexDir, fileName)
                if (extractedFile.isFile) {
                    sourcePaths.add(extractedFile.absolutePath)
                } else {
                    throw IOException("Missing extracted secondary dex file '" + extractedFile.path + "'")
                }
            }
        }
        if (BuildConfig.DEBUG) {
            sourcePaths.addAll(tryLoadInstantRunDexFile(applicationInfo))
        }
        return sourcePaths
    }

    @SuppressLint("PrivateApi")
    private fun tryLoadInstantRunDexFile(applicationInfo: ApplicationInfo): List<String> {
        val instantRunSourcePaths: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != applicationInfo.splitSourceDirs) {
            instantRunSourcePaths.addAll(listOf(*applicationInfo.splitSourceDirs))
        } else {
            try {
                val pathsByInstantRun = Class.forName("com.android.tools.fd.runtime.Paths")
                val getDexFileDirectory = pathsByInstantRun.getMethod("getDexFileDirectory", String::class.java)
                val instantRunDexPath = getDexFileDirectory.invoke(null, applicationInfo.packageName) as String
                val instantRunFilePath = File(instantRunDexPath)
                if (instantRunFilePath.exists() && instantRunFilePath.isDirectory) {
                    val dexFile = instantRunFilePath.listFiles() ?: emptyArray()
                    for (file in dexFile) {
                        if (null != file &&
                            file.exists() &&
                            file.isFile &&
                            file.name.endsWith(".dex")
                        ) {
                            instantRunSourcePaths.add(file.absolutePath)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
        return instantRunSourcePaths
    }

    private val isVMMultidexCapable: Boolean
        get() {
            var isMultidexCapable = false
            try {
                if (isYunOS) {
                    isMultidexCapable = Integer.valueOf(System.getProperty("ro.build.version.sdk")!!) >= 21
                } else {
                    val versionString = System.getProperty("java.vm.version")
                    if (versionString != null) {
                        val matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(versionString)
                        if (matcher.matches()) {
                            try {
                                val major = matcher.group(1)?.toInt() ?: 0
                                val minor = matcher.group(2)?.toInt() ?: 0
                                isMultidexCapable = (major > VM_WITH_MULTIDEX_VERSION_MAJOR
                                        || (major == VM_WITH_MULTIDEX_VERSION_MAJOR
                                        && minor >= VM_WITH_MULTIDEX_VERSION_MINOR))
                            } catch (ignore: NumberFormatException) {
                            }
                        }
                    }
                }
            } catch (ignore: Exception) {
            }
            return isMultidexCapable
        }

    private val isYunOS: Boolean
        get() = try {
            val version = System.getProperty("ro.yunos.version")
            val vmName = System.getProperty("java.vm.name")
            (vmName != null && vmName.toLowerCase(Locale.getDefault()).contains("lemur")
                    || version != null && version.trim { it <= ' ' }.isNotEmpty())
        } catch (ignore: Exception) {
            false
        }
}