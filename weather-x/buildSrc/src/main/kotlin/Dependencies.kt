import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

object Plugins {
    private const val GRADLE_VERSION = "4.0.0"
    private const val KOTLIN_VERSION = "1.3.72"
    private const val HILT_VERSION = "2.28-alpha"
    private const val NAVIGATION_SAFE_ARGS_VERSION = "2.3.0-rc01"

    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_VERSION"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
    const val HILT = "com.google.dagger:hilt-android-gradle-plugin:$HILT_VERSION"
    const val NAVIGATION_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAVIGATION_SAFE_ARGS_VERSION"
}

object Build {
    const val SDK_VERSION = 29
    const val MIN_SDK_VERSION = 23
    const val TARGET_SDK_VERSION = 29
    const val BUILD_TOOLS_VERSION = "29.0.3"
    const val APPLICATION_ID = "org.xapps.apps.weatherx"
    const val APP_NAME = "WeatherX"
    const val MAJOR_VERSION = 0
    const val MINOR_VERSION = 1
    const val PATH_VERSION = 0
    const val STATUS_VERSION = ""
}

object Repositories {
    fun addBuildScriptRepositories(handler: RepositoryHandler) {
        handler.google()
        handler.jcenter()
        //handler.gradlePluginPortal()  // Unstable
        handler.maven(url = "https://plugins.gradle.org/m2")
        handler.maven(url = "https://maven.fabric.io/public")
        handler.maven(url = "https://clojars.org/repo/")
        handler.maven(url = "https://repo1.maven.org/maven2")
        handler.maven(url = "https://jitpack.io")
    }

    fun addProjectRepositories(handler: RepositoryHandler) {
        handler.google()
        handler.jcenter()
        handler.mavenCentral()
        handler.maven(url = "https://clojars.org/repo/")
        handler.maven(url = "https://repo1.maven.org/maven2")
        handler.maven(url = "https://jitpack.io")
        handler.maven(url = "http://dl.bintray.com/piasy/maven")
    }
}

object Libraries {
    object Kotlin {
        private const val CORE_VERSION = "1.3.0"
        private const val COROUTINES_VERSION = "1.3.5"

        const val CORE = "androidx.core:core-ktx:$CORE_VERSION"
        const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
        const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
    }

    object Jetpack {
        object MultiDex {
            private const val VERSION = "2.0.1"

            const val CORE = "androidx.multidex:multidex:$VERSION"
        }

        object UI {
            private const val CONSTRAINT_LAYOUT_VERSION = "2.0.0-beta6"
            private const val MATERIAL_VERSION = "1.2.0-alpha06"
            private const val APP_COMPAT_VERSION = "1.1.0"
            private const val RECYCLER_VIEW_VERSION = "1.1.0"
            private const val CARD_VIEW_VERSION = "1.0.0"

            const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION"
            const val MATERIAL = "com.google.android.material:material:$MATERIAL_VERSION"
            const val APP_COMPAT = "androidx.appcompat:appcompat:$APP_COMPAT_VERSION"
            const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:$RECYCLER_VIEW_VERSION"
            const val CARD_VIEW = "androidx.cardview:cardview:$CARD_VIEW_VERSION"
        }
        
        object Extensions {
            private const val ACTIVITY_KOTLIN_EXT_VERSION = "1.1.0"
            private const val FRAGMENT_KOTLIN_EXT_VERSION = "1.2.4"

            const val ACTIVITY_KOTLIN_EXT = "androidx.activity:activity-ktx:$ACTIVITY_KOTLIN_EXT_VERSION"
            const val FRAGMENT_KOTLIN_EXT = "androidx.fragment:fragment-ktx:$FRAGMENT_KOTLIN_EXT_VERSION"
        }

        object Navigation {
            private const val VERSION = "2.2.2"

            const val UI = "androidx.navigation:navigation-ui:$VERSION"
            const val UI_KTX = "androidx.navigation:navigation-ui-ktx:$VERSION"
            const val FRAGMENT = "androidx.navigation:navigation-fragment:$VERSION"
            const val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$VERSION"
        }

        object AnnotationSupport {
            private const val ANNOTATION_VERSION = "1.1.0"
            private const val LEGACY_SUPPORT_VERSION = "1.0.0"

            const val ANNOTATION = "androidx.annotation:annotation:$ANNOTATION_VERSION"
            const val LEGACY_SUPPORT = "androidx.legacy:legacy-support-v4:$LEGACY_SUPPORT_VERSION"
        }

        object SharedPreferences {
            private const val VERSION = "1.1.1"

            const val CORE = "androidx.preference:preference:$VERSION"
            const val CORE_KTX = "androidx.preference:preference-ktx:$VERSION"
        }

        object Lifecycle {
            private const val VERSION = "2.2.0"

            const val RUNTIME = "androidx.lifecycle:lifecycle-runtime:$VERSION"
            const val EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:$VERSION"
            const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel:$VERSION"
            const val VIEWMODEL_SAVED_STATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$VERSION"
            const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
            const val COMPILER = "androidx.lifecycle:lifecycle-compiler:$VERSION"
        }

        object Room {
            private const val VERSION = "2.2.5"

            const val RUNTIME = "androidx.room:room-runtime:$VERSION"
            const val CORE_KTX = "androidx.room:room-ktx:$VERSION"
            const val COMPILER = "androidx.room:room-compiler:$VERSION"
            const val RXJAVA = "androidx.room:room-rxjava2:$VERSION"
        }
    }

    object Dagger {
        private const val VERSION = "2.27"

        const val CORE = "com.google.dagger:dagger:$VERSION"
        const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
        const val ANDROID = "com.google.dagger:dagger-android:$VERSION"
        const val ANDROID_SUPPORT = "com.google.dagger:dagger-android-support:$VERSION"
        const val ANDROID_PROCESSOR = "com.google.dagger:dagger-android-processor:$VERSION"
    }

    object Hilt {
        private const val VERSION = "2.28-alpha"
        private const val JETPACK_VERSION = "1.0.0-alpha01"

        const val CORE = "com.google.dagger:hilt-android:$VERSION"
        const val COMPILER = "com.google.dagger:hilt-android-compiler:$VERSION"
        const val VIEWMODEL = "androidx.hilt:hilt-lifecycle-viewmodel:$JETPACK_VERSION"
        const val JETPACK_COMPILER = "androidx.hilt:hilt-compiler:$JETPACK_VERSION"
    }

    object Retrofit {
        private const val VERSION = "2.8.1"
        private const val LOGGING_INTERCEPTOR_VERSION = "4.5.0"

        const val CORE = "com.squareup.retrofit2:retrofit:$VERSION"
        const val MOSHI = "com.squareup.retrofit2:converter-moshi:$VERSION"
        const val GSON = "com.squareup.retrofit2:converter-gson:$VERSION"
        const val SCALARS = "com.squareup.retrofit2:converter-scalars:$VERSION"
        const val RXJAVA = "com.squareup.retrofit2:adapter-rxjava2:$VERSION"
        const val LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:$LOGGING_INTERCEPTOR_VERSION"

    }

    object Moshi {
        private const val VERSION = "1.9.2"

        const val MOSHI = "com.squareup.moshi:moshi:$VERSION"
        const val COMPILER = "com.squareup.moshi:moshi-kotlin-codegen:$VERSION"
        const val MOSHI_KTX = "com.squareup.moshi:moshi-kotlin:$VERSION"
    }

    object RxJava {
        private const val RXJAVA_VERSION = "2.2.19"
        private const val RXANDROID_VERSION = "2.1.1"
        private const val RXKOTLIN_VERSION = "2.4.0"

        const val RXJAVA = "io.reactivex.rxjava2:rxjava:$RXJAVA_VERSION"
        const val RXANDROID = "io.reactivex.rxjava2:rxandroid:$RXANDROID_VERSION"
        const val RXKOTLIN = "io.reactivex.rxjava2:rxkotlin:$RXKOTLIN_VERSION"
    }

    object ScalableUnits {
        private const val VERSION = "1.0.6"

        const val DP = "com.intuit.sdp:sdp-android:$VERSION"
        const val SP = "com.intuit.ssp:ssp-android:$VERSION"
    }

    object UI {
        private const val LOOPING_VIEW_PAGER_VERSION = "1.3.1"
        private const val PAGE_INDICATOR_VIEW_VERSION = "1.0.3"
        private const val PICASSO_VERSION = "2.71828"
        private const val UCROP_VERSION = "2.2.5-native"
        private const val MP_ANDROID_CHART_VERSION = "3.1.0"
        private const val FLOW_LAYOUT_VERSION = "0.4.1"
        private const val PROGRESS_BUTTON_VERSION = "2.1.0"
        private const val MATERIAL_PROGRESS_BAR_VERSION = "1.6.1"
        private const val ACTIVITY_CIRCULAR_REVEAL_VERSION = "1.0.2"
        private const val SPINKIT_VERSION = "1.4.0"
        private const val OVERSCROLL_DECOR_VERSION = "1.0.4"
        private const val FILE_PICKER_VERSION = "v8.0.19"
        private const val MATERIAL_RATING_VERSION = "1.4.0"

        const val LOOPING_VIEW_PAGER = "com.asksira.android:loopingviewpager:$LOOPING_VIEW_PAGER_VERSION"
        const val PAGE_INDICATOR_VIEW = "com.romandanylyk:pageindicatorview:$PAGE_INDICATOR_VIEW_VERSION"
        const val PICASSO = "com.squareup.picasso:picasso:$PICASSO_VERSION"
        const val UCROP = "com.github.yalantis:ucrop:$UCROP_VERSION"
        const val MP_ANDROID_CHART = "com.github.PhilJay:MPAndroidChart:$MP_ANDROID_CHART_VERSION"
        const val FLOW_LAYOUT = "com.wefika:flowlayout:$FLOW_LAYOUT_VERSION"
        const val PROGRESS_BUTTON = "com.github.razir.progressbutton:progressbutton:$PROGRESS_BUTTON_VERSION"
        const val MATERIAL_PROGRESS_BAR = "me.zhanghai.android.materialprogressbar:library:$MATERIAL_PROGRESS_BAR_VERSION"
        const val ACTIVITY_CIRCULAR_REVEAL = "com.github.tombayley:ActivityCircularReveal:$ACTIVITY_CIRCULAR_REVEAL_VERSION"
        const val SPINKIT = "com.github.ybq:Android-SpinKit:$SPINKIT_VERSION"
        const val OVERSCROLL_DECOR = "me.everything:overscroll-decor-android:$OVERSCROLL_DECOR_VERSION"
        const val FILE_PICKER = "com.github.TutorialsAndroid:FilePicker:$FILE_PICKER_VERSION"
        const val MATERIAL_RATING = "me.zhanghai.android.materialratingbar:library:$MATERIAL_RATING_VERSION"
    }

    object MaterialDialog {
        private const val CORE_VERSION = "3.3.0"

        const val CORE = "com.afollestad.material-dialogs:core:$CORE_VERSION"
        const val INPUT = "com.afollestad.material-dialogs:input:$CORE_VERSION"
        const val FILES = "com.afollestad.material-dialogs:files:$CORE_VERSION"
        const val COLORS = "com.afollestad.material-dialogs:color:$CORE_VERSION"
        const val DATETIME = "com.afollestad.material-dialogs:datetime:$CORE_VERSION"
        const val BOTTOMSHEETS = "com.afollestad.material-dialogs:bottomsheets:$CORE_VERSION"
        const val LIFECYCLE = "com.afollestad.material-dialogs:lifecycle:$CORE_VERSION"
    }

    object Glide {
        private const val GLIDE_VERSION = "4.11.0"

        const val GLIDE = "com.github.bumptech.glide:glide:$GLIDE_VERSION"
        const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:$GLIDE_VERSION"
    }

    object QRs {
        private const val ZXING_VERSION = "3.4.0"
        private const val ZXING_ANDROID_EMBEDDED_VERSION = "4.1.0"

        const val ZXING = "com.google.zxing:core:$ZXING_VERSION"
        const val ZXING_ANDROID_EMBEDDED = "com.journeyapps:zxing-android-embedded:$ZXING_ANDROID_EMBEDDED_VERSION"
    }

    object OkSocket {
        private const val SERVER_VERSION = "4.2.3"
        private const val CLIENT_VERSION = "4.2.3"

        const val SERVER = "com.tonystark.android:socket-server:$SERVER_VERSION"
        const val CLIENT = "com.tonystark.android:socket:$CLIENT_VERSION"
    }

    object Permissions {
        private const val DEXTER_VERSION = "6.1.0"

        const val DEXTER = "com.karumi:dexter:$DEXTER_VERSION"
    }

    object Logger {

        object Timber {
            private const val VERSION = "4.7.1"

            const val CORE = "com.jakewharton.timber:timber:$VERSION"
        }

    }

    object Others {
        private const val JTRANSFORM_VERSION = "3.1"
        private const val SCANNER_COMPAT_VERSION = "1.4.2"
        private const val APACHE_COMMONS_LANG_VERSION = "3.10"
        private const val COMPLEX_NUMBERS_VERSION = "1.0"

        const val JTRANSFORM = "com.github.wendykierp:JTransforms:$JTRANSFORM_VERSION"
        const val SCANNER_COMPAT = "no.nordicsemi.android.support.v18:scanner:$SCANNER_COMPAT_VERSION"
        const val APACHE_COMMONS_LANG = "org.apache.commons:commons-lang3:$APACHE_COMMONS_LANG_VERSION"
        const val COMPLEX_NUMBER = "org.kotlinmath:complex-numbers:$COMPLEX_NUMBERS_VERSION"
    }

    object Crash {
        private const val SENTRY_VERSION = "2.0.0"
        const val SENTRY = "io.sentry:sentry-android:$SENTRY_VERSION"
    }
}

object Test {
    object Junit {
        private const val VERSION = "4.12"
        private const val EXT_VERSION = "1.1.1"
        const val  JUNIT = "junit:junit:$VERSION"
        const val EXT_JUNIT = "androidx.test.ext:junit:$EXT_VERSION"
    }

    object Espresso {
        private const val VERSION = "3.1.1"
        const val CORE = "androidx.test.espresso:espresso-core:$VERSION"
        const val CONTRIB = "androidx.test.espresso:espresso-contrib:$VERSION"
        const val INTENTS = "androidx.test.espresso:espresso-intents:$VERSION"
    }

    object Gson {
        private const val VERSION = "2.8.5"
        const val  GSON = "com.google.code.gson:gson:$VERSION"
    }

    object Coroutines {
        private const val VERSION = "1.3.7"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$VERSION"
    }
}