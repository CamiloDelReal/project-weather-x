buildscript {
    Repositories.addBuildScriptRepositories(repositories)

    dependencies {
        classpath(Plugins.GRADLE)
        classpath(Plugins.KOTLIN)
        classpath(Plugins.HILT)
        classpath(Plugins.NAVIGATION_SAFE_ARGS)
    }
}

allprojects {
    Repositories.addProjectRepositories(repositories)
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}