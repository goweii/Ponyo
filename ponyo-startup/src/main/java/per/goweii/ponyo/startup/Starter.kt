package per.goweii.ponyo.startup

import android.app.Activity
import android.app.Application
import android.os.Process
import androidx.fragment.app.Fragment
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

    private var activityStarter: ActivityStarter? = null

    private val initMetas = mutableMapOf<String, InitMeta>()
    private val activityInitMap = mutableMapOf<String, ArrayList<String>>()
    private val fragmentInitMap = mutableMapOf<String, ArrayList<String>>()
    private val initializerCache = mutableMapOf<String, Initializer>()

    internal fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        findInitMetas().forEach { initMetas[it.className] = it }
        activityStarter = ActivityStarter.create(application)
        val initRunner = InitRunner()
        initMetas.values.forEach { initMeta ->
            if (initMeta.activities.isNullOrEmpty() && initMeta.fragments.isNullOrEmpty()) {
                initRunner.add(initMeta.className)
            } else {
                for (activity in initMeta.activities) {
                    activityInitMap[activity]?.add(initMeta.className)
                        ?: run { activityInitMap[activity] = arrayListOf(initMeta.className) }
                }
                for (fragment in initMeta.fragments) {
                    fragmentInitMap[fragment]?.add(initMeta.className)
                        ?: run { fragmentInitMap[fragment] = arrayListOf(initMeta.className) }
                }
            }
        }
        initRunner.run()
    }

    fun initializeFollowActivity(activityClass: Class<out Activity>) {
        val activityName = activityClass.name
        activityInitMap[activityName]?.let { list ->
            val initRunner = InitRunner()
            list.forEach {
                if (!isInitialized(it)) {
                    initRunner.add(it)
                }
            }
            initRunner.run()
        }
    }

    fun initializeFollowFragment(fragmentClass: Class<out Fragment>) {
        val fragmentName = fragmentClass.name
        fragmentInitMap[fragmentName]?.let { list ->
            val initRunner = InitRunner()
            list.forEach {
                if (!isInitialized(it)) {
                    initRunner.add(it)
                }
            }
            initRunner.run()
        }
    }

    fun initialize(block: Application.(isMainProcess: Boolean) -> Unit) {
        block.invoke(application, isMainProcess())
    }

    fun initialize(initializerClass: Class<out Initializer>) {
        val initializerName = initializerClass.name
        initialize(initializerName)
    }

    fun initialize(initializerName: String) {
        if (!isInitialized(initializerName)) {
            val initializer = getOrCreateInitializer(initializerName)
            initialize(initializer)
        }
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
                Ponlog.i { "start init ${initializer::class.java.name}" }
                initializer.initialize(application, isMainProcess())
                setInitialized(initializer::class.java.name)
                Ponlog.i { "success init ${initializer::class.java.name}" }
            } catch (e: Throwable) {
                Ponlog.e { "error init ${initializer::class.java.name}" }
                e.printStackTrace()
            }
        }
    }

    private fun setInitialized(className: String) {
        initMetas[className]?.isInitialized = true
        initializerCache.remove(className)
        val activityInitIt = activityInitMap.iterator()
        while (activityInitIt.hasNext()) {
            val entry = activityInitIt.next()
            entry.value.remove(className)
            if (entry.value.isEmpty()) {
                activityInitIt.remove()
            }
        }
        val fragmentInitIt = fragmentInitMap.iterator()
        while (fragmentInitIt.hasNext()) {
            val entry = fragmentInitIt.next()
            entry.value.remove(className)
            if (entry.value.isEmpty()) {
                fragmentInitIt.remove()
            }
        }
        if (activityInitMap.isEmpty() && fragmentInitMap.isEmpty()) {
            activityStarter?.recycle(application)
        }
    }

    private fun isInitialized(className: String): Boolean {
        return initMetas[className]?.isInitialized ?: false
    }

    internal fun getOrCreateInitializer(className: String): Initializer {
        var initializer = initializerCache[className]
        if (initializer == null) {
            val initCls = Class.forName(className)
            initializer = initCls.newInstance() as Initializer
            initializerCache[className] = initializer
        }
        return initializer
    }

    private fun findInitMetas(): Set<InitMeta> {
        return findFromPackage()
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