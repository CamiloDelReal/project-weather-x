package org.xapps.apps.weatherx.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import org.xapps.apps.weatherx.BuildConfig
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentAboutBinding
import javax.inject.Inject


@AndroidEntryPoint
class AboutFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentAboutBinding

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindings = FragmentAboutBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.version = BuildConfig.VERSION_NAME
        return bindings.root
    }

    private fun back() {
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.activity_enter_pop, R.anim.activity_exit_pop)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                back()
            }
        }

        bindings.btnBack.setOnClickListener {
            back()
        }

        bindings.btnAboutLink.setOnClickListener {
            launchUri(getString(R.string.project_github))
        }

        bindings.btnLinkOpenWeather.setOnClickListener {
            launchUri(getString(R.string.openweathermap_url))
        }

        bindings.btnLinkGoogleFonts.setOnClickListener {
            launchUri(getString(R.string.quicksand_url))
        }

        bindings.btnLinkMaterialDesignIcons.setOnClickListener {
            launchUri(getString(R.string.material_design_icons_url))
        }

        bindings.btnLinkLottieFiles1.setOnClickListener {
            launchUri(getString(R.string.lottiefiles_jochang_url))
        }

        bindings.btnLinkLottieFiles2.setOnClickListener {
            launchUri(getString(R.string.lottiefiles_kerembalku_url))
        }

        bindings.btnLinkUiGradients.setOnClickListener {
            launchUri(getString(R.string.uigradients_url))
        }

        bindings.btnLinkAndroidJetpack.setOnClickListener {
            launchUri(getString(R.string.android_jetpack_url))
        }

        bindings.btnLinkAndroidKotlin.setOnClickListener {
            launchUri(getString(R.string.android_kotlin_url))
        }

        bindings.btnLinkGooglePlayLocation.setOnClickListener {
            launchUri(getString(R.string.android_google_play_service_location_url))
        }

        bindings.btnLinkDexterPermissions.setOnClickListener {
            launchUri(getString(R.string.dexter_url))
        }

        bindings.btnLinkRetrofit.setOnClickListener {
            launchUri(getString(R.string.retrofit_url))
        }

        bindings.btnLinkMoshi.setOnClickListener {
            launchUri(getString(R.string.moshi_url))
        }

        bindings.btnLinkShapeOfView.setOnClickListener {
            launchUri(getString(R.string.shape_of_view_url))
        }

        bindings.btnLinkLottieForAndroid.setOnClickListener {
            launchUri(getString(R.string.lottie_for_android_url))
        }

        bindings.btnLinkToasty.setOnClickListener {
            launchUri(getString(R.string.toasty_url))
        }

        bindings.btnLinkTimber.setOnClickListener {
            launchUri(getString(R.string.timber_url))
        }
    }

    private fun launchUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}