package org.xapps.apps.weatherx.views.popups

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.content_popup_more_options.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.settings.SettingsService
import javax.inject.Inject


@AndroidEntryPoint
class MoreOptionsPopup @Inject constructor() : DialogFragment() {

    @Inject
    lateinit var settings: SettingsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val style = STYLE_NO_FRAME
        val theme = R.style.PopupStyle
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentView = inflater.inflate(R.layout.content_popup_more_options, container, false)

        return currentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDarkMode.isChecked = settings.isDarkModeOn()
        btnDarkMode.addOnCheckedChangeListener { button, isChecked ->
            settings.setIsDarkModeOn(isChecked)
            val data = Intent().apply {
                putExtra(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_DARK_MODE_UPDATED)
            }
            close(MORE_OPTIONS_POPUP_ACCEPTED_CODE, data)
        }

        rootLayout.setOnClickListener {
            close(MORE_OPTIONS_POPUP_NOT_ACCEPTED_CODE)
        }

        btnAbout.setOnClickListener {
            val data = Intent().apply {
                putExtra(MORE_OPTIONS_POPUP_OPTION, MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW)
            }
            close(MORE_OPTIONS_POPUP_ACCEPTED_CODE, data)
        }
    }

    private fun close(code: Int, data: Intent? = null) {
        targetFragment!!.onActivityResult(targetRequestCode, code, data)
        dismiss()
    }

    override fun onResume() {
        val window = dialog!!.window
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setGravity(Gravity.CENTER)

        dialog?.apply {
            setOnKeyListener { _, keyCode, _ ->
                if ((keyCode ==  KeyEvent.KEYCODE_BACK))
                {
                    close(MORE_OPTIONS_POPUP_NOT_ACCEPTED_CODE)
                    true
                }
                else false
            }
        }

        super.onResume()
    }

    companion object {
        const val MORE_OPTIONS_POPUP_CODE = 200
        const val MORE_OPTIONS_POPUP_ACCEPTED_CODE = 201
        const val MORE_OPTIONS_POPUP_NOT_ACCEPTED_CODE = 202
        const val MORE_OPTIONS_POPUP_OPTION = "moreOptionsPopupOption"
        const val MORE_OPTIONS_POPUP_DARK_MODE_UPDATED = 203
        const val MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW = 204
    }
}