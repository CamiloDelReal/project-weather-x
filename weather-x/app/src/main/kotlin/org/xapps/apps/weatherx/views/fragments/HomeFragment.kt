package org.xapps.apps.weatherx.views.fragments

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentHomeBinding
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.viewmodels.HomeViewModel
import org.xapps.apps.weatherx.views.bindings.ConstraintLayoutBindings
import org.xapps.apps.weatherx.views.bindings.LottieAnimationViewBindings
import org.xapps.apps.weatherx.views.popups.MoreOptionsPopup
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var settings: SettingsService

    private var lastCompletedConstraint: Int? = null
    private var lastConditionBottomColor: Int? = null
    private lateinit var navigationBarColorAnimation: ValueAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareForLoading()

        motionFg.setTransitionListener(object : MotionLayout.TransitionListener {

            override fun onTransitionTrigger(
                layout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

            override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(
                layout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                if (startId == R.id.setBegin && endId == R.id.setHalf) {
                    motionBg.setTransition(R.id.setBegin, R.id.setHalf)
                    motionBg.progress = progress
                    Handler(Looper.getMainLooper()).post(Runnable {
                        navigationBarColorAnimation.setCurrentFraction(progress)
                    })
                } else if (startId == R.id.setHalf && endId == R.id.setEnd) {
                    motionBg.setTransition(R.id.setHalf, R.id.setEnd)
                    motionBg.progress = progress
                }
            }

            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
                if (lastCompletedConstraint != null) {
                    when {
                        (currentId == R.id.setBegin && (lastCompletedConstraint == R.id.setBegin || lastCompletedConstraint == R.id.setHalf)) -> {
                            motionBg.progress = 0.0f
                            navigationBarColorAnimation.setCurrentFraction(0.0f)
                        }
                        (currentId == R.id.setHalf && lastCompletedConstraint == R.id.setBegin) -> {
                            motionBg.progress = 1.0f
                            navigationBarColorAnimation.setCurrentFraction(1.0f)
                        }
                        (currentId == R.id.setHalf && lastCompletedConstraint == R.id.setEnd) -> {
                            motionBg.progress = 0.0f
                        }
                        (currentId == R.id.setEnd && (lastCompletedConstraint == R.id.setEnd || lastCompletedConstraint == R.id.setHalf)) -> {
                            motionBg.progress = 1.0f
                        }
                    }
                }
                lastCompletedConstraint = currentId
            }

        })

        viewModel.watchWorking().observe(viewLifecycleOwner, { isWorking ->

        })

        viewModel.watchError().observe(viewLifecycleOwner, { errorMessage ->
            if (motionFg.currentState == R.id.setLoading) {
                txvError.text = errorMessage
                txvError.visibility = View.VISIBLE
                btnTryAgain.visibility = View.VISIBLE
            } else {
                Toasty.error(requireContext(), errorMessage, Toast.LENGTH_LONG, true).show()
                Toasty.custom(
                    requireContext(),
                    errorMessage,
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_information_outline
                    ),
                    ContextCompat.getColor(requireContext(), R.color.red_500),
                    ContextCompat.getColor(requireContext(), R.color.white),
                    Toasty.LENGTH_LONG,
                    true,
                    true
                ).show()
            }
        })

        viewModel.watchReady().observe(viewLifecycleOwner, { isReady ->
            if (isReady) {
                updateNavigationBarColor(false, true)
                if (motionFg.currentState == R.id.setLoading) {
                    txvError.visibility = View.INVISIBLE
                    btnTryAgain.visibility = View.INVISIBLE
                    motionFg.transitionToEnd()
                }
            }
        })

        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    lifecycleScope.launchWhenResumed {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                viewModel.prepareMonitoring()
                            }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(requireContext(), it.name, Toast.LENGTH_LONG).show()
            }
            .check()

        btnAdd.setOnClickListener {
            val moreOptionPopup = MoreOptionsPopup()
            moreOptionPopup.setTargetFragment(
                this@HomeFragment,
                MoreOptionsPopup.MORE_OPTIONS_POPUP_CODE
            )
            val fragmentManager = parentFragmentManager.beginTransaction()
            moreOptionPopup.show(fragmentManager, "MoreOptionsPopup")
        }

        btnTryAgain.setOnClickListener {
            viewModel.prepareMonitoring()
        }
    }

    fun prepareForLoading() {
        val lastAnimation = LottieAnimationViewBindings.weatherAnimation(
            viewModel.lastConditionCode(),
            viewModel.lastWasDayLight(),
            viewModel.lastWasThereVisibility()
        )
        lotConditionImage.tag = lastAnimation
        lotConditionImage.setAnimation(lastAnimation)
        lotConditionImage.speed = 1.0f
        lotConditionImage.repeatCount = LottieDrawable.INFINITE
        lotConditionImage.repeatMode = LottieDrawable.RESTART
        lotConditionImage.playAnimation()
        val lastBackground = ConstraintLayoutBindings.conditionBackground(
            viewModel.lastConditionCode(),
            viewModel.lastWasDayLight(),
            viewModel.lastTemperature(),
            viewModel.useMetricSystem()
        )
        rootLayout.setBackgroundResource(lastBackground)
        updateNavigationBarColor()
    }

    private fun updateNavigationBarColor(useSurface: Boolean = true, animate: Boolean = false) {
        val previousBottomColor = lastConditionBottomColor
        lastConditionBottomColor = ConstraintLayoutBindings.conditionBottomColor(
            viewModel.lastConditionCode(),
            viewModel.lastWasDayLight(),
            viewModel.lastTemperature(),
            viewModel.useMetricSystem()
        )
        if (motionFg.currentState in arrayOf(R.id.setLoading, R.id.setBegin)) {
            if (animate) {
                val typedValue = TypedValue()
                val theme = requireContext().theme
                theme.resolveAttribute(R.attr.colorSurface, typedValue, true)
                val colorSurface = typedValue.data
                val colorFrom = if (useSurface) {
                    colorSurface
                } else {
                    ContextCompat.getColor(requireContext(), previousBottomColor!!)
                }
                val colorTo = ContextCompat.getColor(requireContext(), lastConditionBottomColor!!)
                val colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo)
                colorAnimation.startDelay = 400
                colorAnimation.duration = 300
                colorAnimation.addUpdateListener { animator ->
                    requireActivity().window.navigationBarColor = animator.animatedValue as Int
                }
                navigationBarColorAnimation = ValueAnimator.ofArgb(colorTo, colorSurface)
                navigationBarColorAnimation.duration = 250
                navigationBarColorAnimation.addUpdateListener { animator ->
                    requireActivity().window.navigationBarColor = animator.animatedValue as Int
                }
                colorAnimation.start()
            } else {
                requireActivity().window.navigationBarColor = ContextCompat.getColor(
                    requireContext(),
                    lastConditionBottomColor!!
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MoreOptionsPopup.MORE_OPTIONS_POPUP_CODE) {
            if (resultCode == MoreOptionsPopup.MORE_OPTIONS_POPUP_ACCEPTED_CODE) {
                val option = data?.getIntExtra(MoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION, -1) ?: -1
                when (option) {
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_METRIC_SYSTEM_UPDATED -> {
                        viewModel.prepareMonitoring()
                    }
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_DARK_MODE_UPDATED -> {
                        AppCompatDelegate.setDefaultNightMode(if (settings.isDarkModeOn()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCommonActivity())
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopJobGpsTrackerScheduler()
        viewModel.stopJobWeatherScheduler()
    }
}