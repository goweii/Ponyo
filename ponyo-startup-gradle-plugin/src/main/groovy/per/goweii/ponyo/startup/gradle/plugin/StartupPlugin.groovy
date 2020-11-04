package per.goweii.ponyo.startup.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class StartupPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        System.out.println("========================")
        System.out.println("StartupPlugin groovy")
        System.out.println("========================")
    }
}