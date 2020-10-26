package org.xapps.apps.weatherx.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_about.*
import org.xapps.apps.weatherx.BuildConfig
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentAboutBinding
import javax.inject.Inject


@AndroidEntryPoint
class AboutFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    val f: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.version = BuildConfig.VERSION_NAME
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            findNavController().navigateUp()
            requireActivity().finish()
        }

        btnLinkOpenWeather.setOnClickListener {
            launchUri(getString(R.string.openweathermap_url))
        }

        btnLinkMaterialDesignIcons.setOnClickListener {
            launchUri(getString(R.string.material_design_icons_url))
        }

        btnLinkLottieFiles.setOnClickListener {
            launchUri(getString(R.string.lottiefiles_jochang_url))
        }

        btnLinkUiGradients.setOnClickListener {
            launchUri(getString(R.string.uigradients_url))
        }
    }

    private fun launchUri(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intent)
    }
}