package per.goweii.ponyo.startup

import android.app.Application
import android.os.Process
import per.goweii.ponyo.startup.annotation.Const
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object Startup {

    private lateinit var application: Application

    internal fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        val initializerRunner = InitializerRunner(application)
        if (BuildConfig.DEBUG) {
            findFromPackage().forEach { initName ->
                initializerRunner.add(newInitializer(initName))
            }
        } else {
            val sp = InitializerPreferences(application)
            sp.getInitializers()?.let { initializes ->
                initializes.forEach { initName ->
                    initializerRunner.add(newInitializer(initName))
                }
            } ?: findFromPackage().let { initializes ->
                sp.setInitializers(initializes)
                initializes.forEach { initName ->
                    initializerRunner.add(newInitializer(initName))
                }
            }
        }
        initializerRunner.run()
    }

    private fun findFromPackage(): Set<String> {
        val set = mutableSetOf<String>()
        val holders = ClassFinder.findAllClassByPackageName(application, Const.GENERATED_PACKAGE_NAME)
        holders.forEach { holderName ->
            val holderCls = Class.forName(holderName)
            val holder = holderCls.getConstructor().newInstance()
            val inits = holderCls.getField(Const.GENERATED_LIST_FIELD).get(holder) as List<*>
            inits.forEach { initName ->
                initName as String
                set.add(initName)
            }
        }
        return set
    }

    private fun newInitializer(className: String): Initializer {
        val initCls = Class.forName(className)
        val initializer = initCls.newInstance()
        initializer as Initializer
        return initializer
    }

    fun initialize(block: Application.(isMainProcess: Boolean) -> Unit) {
        block.invoke(application, isMainProcess())
    }

    internal fun isMainProcess(): Boolean {
        return application.packageName == getCurrentProcessName()
    }

    private fun getCurrentProcessName(): String? {
        return try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}