package per.goweii.ponyo.startup.gradle.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class StartupPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp) {
            Logger.make(project)
            def android = project.extensions.getByType(AppExtension)
            def startupTransform = new StartupTransform(project)
            android.registerTransform(startupTransform)
        }
    }
}