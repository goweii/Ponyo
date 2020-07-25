package per.goweii.ponyo.startup

import android.app.Activity
import android.app.Application
import android.os.Process
import androidx.fragment.app.Fragment
import per.goweii.ponyo.log.Ponlog
import per.goweii.ponyo.startup.annotation.Const
import per.goweii.ponyo.startup.annotation.InitMetaProvider
import per.goweii.ponyo.startup.annotation.InitMeta
import per.goweii.ponyo.startup.utils.ClassFinder
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object Starter {
    private lateinit var application: Application

    private val initMetas = mutableMapOf<String, InitMeta>()
    private val initializerCache = mutableMapOf<String, Initializer>()

    private lateinit var followStarter: FollowStarter

    internal fun initialize(application: Application) {
        if (this::application.isInitialized) return
        this.application = application
        this.followStarter = FollowStarter(application)
        findInitMetas().forEach { initMetas[it.className] = it }
        val initRunner = InitRunner()
        initMetas.values.forEach { initMeta ->
            if (initMeta.activities.isNullOrEmpty() && initMeta.fragments.isNullOrEmpty()) {
                initRunner.add(initMeta)
            } else {
                followStarter.addInitMeta(initMeta)
            }
        }
        initRunner.run()
    }

    fun getInitMeta(className: String): InitMeta? {
        return initMetas[className]
    }

    fun initializeFollowActivity(activityClass: Class<out Activity>) {
        initializeFollowActivity(activityClass.name)
    }

    fun initializeFollowActivity(activityName: String) {
        val initNames = followStarter.getActivityInitNames(activityName) ?: return
        val initRunner = InitRunner()
        initNames.forEach {
            initMetas[it]?.let { initMeta ->
                if (!initMeta.isInitialized) {
                    initRunner.add(initMeta)
                }
            }
        }
        initRunner.run()
    }

    fun initializeFollowFragment(fragmentClass: Class<out Fragment>) {
        initializeFollowActivity(fragmentClass.name)
    }

    fun initializeFollowFragment(fragmentName: String) {
        val initNames = followStarter.getFragmentInitNames(fragmentName) ?: return
        val initRunner = InitRunner()
        initNames.forEach {
            initMetas[it]?.let { initMeta ->
                if (!initMeta.isInitialized) {
                    initRunner.add(initMeta)
                }
            }
        }
        initRunner.run()
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
        if (isInitialized(initializer::class.java.name)) return
        try {
            Ponlog.i { "start init ${initializer::class.java.name}" }
            initializer.initialize(application, isMainProcess())
            setInitialized(initializer::class.java.name)
            Ponlog.i { "success init ${initializer::class.java.name}" }
        } catch (e: Throwable) {
            e.printStackTrace()
            Ponlog.e { "error init ${initializer::class.java.name}" }
        }
    }

    private fun setInitialized(className: String) {
        val initMeta = initMetas[className]
        initMeta?.isInitialized = true
        initializerCache.remove(className)
        followStarter.setInitialized(className)
    }

    private fun isInitialized(className: String): Boolean {
        return initMetas[className]?.isInitialized ?: false
    }

    private fun getOrCreateInitializer(className: String): Initializer {
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
            holder as InitMetaProvider
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