package org.xapps.apps.weatherx.views.popups

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ContentPopupMoreOptionsBinding
import org.xapps.apps.weatherx.core.repositories.SettingsRepository
import javax.inject.Inject


@AndroidEntryPoint
class MoreOptionsPopup @Inject constructor() : DialogFragment() {

    companion object {
        const val REQUEST_KEY = "MoreOptionsPopup"

        const val MORE_OPTIONS_POPUP_OPTION = "MoreOptionsPopupOption"
        const val MORE_OPTIONS_POPUP_METRIC_SYSTEM_UPDATED = 210
        const val MORE_OPTIONS_POPUP_DARK_MODE_UPDATED = 211
        const val MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW = 212

        fun showDialog(
            fragmentManager: FragmentManager,
            listener: ((requestKey: String, bundle: Bundle) -> Unit)
        ) {
            val popup = MoreOptionsPopup()
            popup.show(fragmentManager, MoreOptionsPopup::class.java.name)
            popup.setFragmentResultListener(REQUEST_KEY, listener)
        }
    }

    private lateinit var bindings: ContentPopupMoreOptionsBinding

    @Inject
    lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val style = STYLE_NO_FRAME
        val theme = R.style.Popup
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentPopupMoreOptionsBinding.inflate(layoutInflater, container, false)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            bindings.btnUseMetric.isChecked = settings.useMetricSystemValue()
            bindings.btnDarkMode.isChecked = settings.isDarkModeOnValue()
        }

        bindings.btnUseMetric.addOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settings.setUseMetricSystem(isChecked)
                val data = Bundle().apply {
                    putInt(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_METRIC_SYSTEM_UPDATED)
                }
                close(data)
            }
        }
        bindings.btnDarkMode.addOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settings.setIsDarkModeOn(isChecked)
                val data = Bundle().apply {
                    putInt(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_DARK_MODE_UPDATED)
                }
                close(data)
            }
        }

        bindings.rootLayout.setOnClickListener {
            close()
        }

        bindings.btnAbout.setOnClickListener {
            val data = Bundle().apply {
                putInt(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW)
            }
            close(data)
        }
    }

    private fun close(data: Bundle = Bundle()) {
        setFragmentResult(REQUEST_KEY, data)
        dismiss()
    }

    override fun onResume() {
        val window = dialog!!.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.CENTER)

        dialog?.apply {
            setOnKeyListener { _, keyCode, _ ->
                if ((keyCode ==  KeyEvent.KEYCODE_BACK))
                {
                    close()
                    true
                }
                else false
            }
        }

        super.onResume()
    }

}