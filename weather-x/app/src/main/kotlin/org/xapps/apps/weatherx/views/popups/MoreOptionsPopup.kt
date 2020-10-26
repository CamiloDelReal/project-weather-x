package org.xapps.apps.weatherx.views.popups

import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.content_popup_more_options.*
import org.xapps.apps.weatherx.R


class MoreOptionsPopup : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val style = STYLE_NO_FRAME
        val theme = R.style.BasicPopupStyle
        setStyle(style, theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentView = inflater.inflate(R.layout.content_popup_more_options, container, false)

        return currentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootLayout.setOnClickListener {
            targetFragment!!.onActivityResult(targetRequestCode, MORE_OPTIONS_POPUP_NOT_ACCEPTED_CODE, null)
            dismiss()
        }

        btnAbout.setOnClickListener {
            targetFragment!!.onActivityResult(targetRequestCode, MORE_OPTIONS_POPUP_ACCEPTED_CODE, null)
            dismiss()
        }
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
                    targetFragment!!.onActivityResult(targetRequestCode, MORE_OPTIONS_POPUP_NOT_ACCEPTED_CODE, null)
                    dismiss()
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
    }
}