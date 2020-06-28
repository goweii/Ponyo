package per.goweii.ponyo.startup

import android.app.Application
import android.os.Process
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.startup.annotation.Const
import per.goweii.ponyo.startup.annotation.InitHolder
import per.goweii.ponyo.startup.annotation.InitMeta
import per.goweii.ponyo.startup.utils.ClassFinder
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object Starter {
    private lateinit var application: Application

    private val initCache = mutableMapOf<String, Initializer?>()
    private val initializedList = arrayListOf<String>()

    internal fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        val initRunner = InitRunner()
        val activityStarter = ActivityStarter(application)
        findFromPackage().forEach { initMeta ->
            if (initMeta.activities.isNullOrEmpty()) {
                initRunner.add(initMeta.className)
            } else {
                for (activity in initMeta.activities) {
                    activityStarter.add(activity, initMeta.className)
                }
            }
        }
        initRunner.run()
    }

    fun initialize(block: Application.(isMainProcess: Boolean) -> Unit) {
        block.invoke(application, isMainProcess())
    }

    fun initialize(initializerClass: Class<out Initializer>) {
        val initializer = getOrCreateInitializer(initializerClass.name)
        initialize(initializer)
    }

    fun initialize(initializerName: String) {
        val initializer = getOrCreateInitializer(initializerName)
        initialize(initializer)
    }

    fun initialize(initializer: Initializer) {
        initializer.depends().forEach {
            if (!isInitialized(it.name)) {
                val dependInitializer = getOrCreateInitializer(it.name)
                initialize(dependInitializer)
            }
        }
        if (!isInitialized(initializer::class.java.name)) {
            try {
                Ponlog.d { "start init ${initializer::class.java.name}" }
                initializer.initialize(application, isMainProcess())
                initializedList.add(initializer::class.java.name)
                Ponlog.d { "success init ${initializer::class.java.name}" }
            } catch (e: Throwable) {
                Ponlog.d { "error init ${initializer::class.java.name}" }
                e.printStackTrace()
            }
        }
    }

    fun isInitialized(className: String): Boolean {
        return initializedList.contains(className)
    }

    internal fun getOrCreateInitializer(className: String): Initializer {
        var initializer = initCache[className]
        if (initializer == null) {
            val initCls = Class.forName(className)
            initializer = initCls.newInstance() as Initializer
            initCache[className] = initializer
        }
        return initializer
    }

    private fun findFromPackage(): Set<InitMeta> {
        val set = mutableSetOf<InitMeta>()
        val holders = ClassFinder.findClasses(application, Const.GENERATED_PACKAGE_NAME)
        holders.forEach { holderName ->
            val holderCls = Class.forName(holderName)
            val holder = holderCls.getConstructor().newInstance()
            holder as InitHolder
            set.add(holder.getInitMeta())
        }
        return set
    }

    private fun isMainProcess(): Boolean {
        return application.packageName == currentProcessName()
    }

    private fun currentProcessName(): String? {
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