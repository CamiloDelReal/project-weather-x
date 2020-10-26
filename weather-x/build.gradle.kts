buildscript {
    val kotlin_version by extra("1.4.10")
    Repositories.addBuildScriptRepositories(repositories)

    dependencies {
        classpath(Plugins.GRADLE)
        classpath(Plugins.KOTLIN)
        classpath(Plugins.HILT)
        classpath(Plugins.NAVIGATION_SAFE_ARGS)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    Repositories.addProjectRepositories(repositories)
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}