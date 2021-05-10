import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.text.SimpleDateFormat
import java.util.*


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(Build.SDK_VERSION)
    buildToolsVersion = Build.BUILD_TOOLS_VERSION

    defaultConfig {
        applicationId = Build.APPLICATION_ID

        minSdkVersion(Build.MIN_SDK_VERSION)
        targetSdkVersion(Build.TARGET_SDK_VERSION)

        versionCode =
            ((Build.MAJOR_VERSION * 10000) + (Build.MINOR_VERSION * 100) + Build.PATH_VERSION)
        versionName =
            "${Build.MAJOR_VERSION}.${Build.MINOR_VERSION}.${Build.PATH_VERSION}${Build.STATUS_VERSION}"

        vectorDrawables.useSupportLibrary = true

        multiDexEnabled = true
//        dexOptions.incremental = true
        dexOptions.preDexLibraries = false
        dexOptions.javaMaxHeapSize = "4g"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    (this as ExtensionAware).configure < KotlinJvmOptions > {
        jvmTarget = "1.8"
    }
    tasks.withType().configureEach {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=org.mylibrary.OptInAnnotation"
    }

    android.buildFeatures.dataBinding = true

    sourceSets["main"].java.srcDirs("src/main/kotlin")
    sourceSets["androidTest"].java.srcDirs("src/androidTest/kotlin")
    sourceSets["test"].java.srcDirs("src/test/kotlin")
    sourceSets["debug"].java.srcDirs("src/debug/kotlin")

    buildTypes["debug"].apply {
        isMinifyEnabled = false
        val openWeatherMapApiKey: String by System.getProperties()
        buildConfigField("String", "OPEN_WEATHER_MAP_API_KEY", openWeatherMapApiKey)
    }

    buildTypes["release"].apply {
        isMinifyEnabled = false
        val openWeatherMapApiKey: String by System.getProperties()
        buildConfigField("String", "OPEN_WEATHER_MAP_API_KEY", openWeatherMapApiKey)

        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )

        applicationVariants.all(object: Action<ApplicationVariant> {
            override fun execute(variant: ApplicationVariant) {
                variant.outputs.all(object: Action<BaseVariantOutput> {
                    override fun execute(output: BaseVariantOutput) {
                        val outputImpl = output as BaseVariantOutputImpl
                        val currentDate = Date()
                        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                        val timestamp = formatter.format(currentDate)
                        val fileName =
                            "${Build.APP_NAME} ${Build.MAJOR_VERSION}.${Build.MINOR_VERSION}.${Build.PATH_VERSION}${Build.STATUS_VERSION} $timestamp.apk"
                        outputImpl.outputFileName = fileName
                    }
                })
            }
        })
    }

}

dependencies {
    // Custom JARs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    // Kotlin
    implementation(kotlin(Libraries.Kotlin.MODULE, KotlinCompilerVersion.VERSION))
    implementation(Libraries.Kotlin.CORE)
    implementation(Libraries.Kotlin.COROUTINES_CORE)
    implementation(Libraries.Kotlin.COROUTINES_ANDROID)

    // Jetpack MultiDex
    implementation(Libraries.Jetpack.MultiDex.CORE)

    // Jetpack Annotations Support
    kapt(Libraries.Jetpack.AnnotationSupport.ANNOTATION)
    implementation(Libraries.Jetpack.AnnotationSupport.LEGACY_SUPPORT)

    // Jetpack UI
    implementation(Libraries.Jetpack.UI.CONSTRAINT_LAYOUT)
    implementation(Libraries.Jetpack.UI.MATERIAL)
    implementation(Libraries.Jetpack.UI.APP_COMPAT)

    // Jetpack Extensions
    implementation(Libraries.Jetpack.Extensions.ACTIVITY_KOTLIN_EXT)
    implementation(Libraries.Jetpack.Extensions.FRAGMENT_KOTLIN_EXT)

    // Jetpack Navigation
    implementation(Libraries.Jetpack.Navigation.UI_KTX)
    implementation(Libraries.Jetpack.Navigation.FRAGMENT_KTX)

    // Jetpack Shared Preferences
    implementation(Libraries.Jetpack.SharedPreferences.CORE_KTX)

    // Jetpack Lifecycle
    implementation(Libraries.Jetpack.Lifecycle.RUNTIME_KTX)
    implementation(Libraries.Jetpack.Lifecycle.EXTENSIONS)
    implementation(Libraries.Jetpack.Lifecycle.VIEWMODEL_KTX)
    implementation(Libraries.Jetpack.Lifecycle.VIEWMODEL_SAVED_STATE)
    implementation(Libraries.Jetpack.Lifecycle.LIVEDATA_KTX)
    kapt(Libraries.Jetpack.Lifecycle.COMPILER)

    // Jetpack Room
    implementation(Libraries.Jetpack.Room.RUNTIME)
    implementation(Libraries.Jetpack.Room.CORE_KTX)
    kapt(Libraries.Jetpack.Room.COMPILER)

    // Dagger
    kapt(Libraries.Dagger.COMPILER)
    implementation(Libraries.Dagger.ANDROID)

    // Hilt
    implementation(Libraries.Hilt.CORE)
    kapt(Libraries.Hilt.COMPILER)

    // Retrofit
    implementation(Libraries.Retrofit.CORE)
    implementation(Libraries.Retrofit.MOSHI)
    implementation(Libraries.Retrofit.LOGGING_INTERCEPTOR)
    implementation(Libraries.Moshi.MOSHI_KTX)

    // Play Services
    implementation(Libraries.PlayServices.LOCATION)

    // Permissions
    implementation(Libraries.Permissions.DEXTER)

    // Lottie
    implementation(Libraries.UI.LOTTIE)

    // Shape of View
    implementation(Libraries.UI.SHAPE_OF_VIEW)

    // Toasty
    implementation(Libraries.UI.TOASTY)

    // Logger
    implementation(Libraries.Logger.Timber.CORE)
}
