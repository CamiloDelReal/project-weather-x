package org.xapps.apps.weatherx.views.fragments

import android.Manifest
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.skip
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentHomeBinding
import org.xapps.apps.weatherx.viewmodels.HomeViewModel
import org.xapps.apps.weatherx.views.bindings.ConstraintLayoutBindings
import org.xapps.apps.weatherx.views.bindings.LottieAnimationViewBindings
import org.xapps.apps.weatherx.views.popups.MoreOptionsPopup
import org.xapps.apps.weatherx.views.utils.Message
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var bindings: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private var lastCompletedConstraint: Int? = null
    private var lastConditionBottomColor: Int? = null
    private lateinit var navigationBarColorAnimation: ValueAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindings = FragmentHomeBinding.inflate(layoutInflater)
        bindings.lifecycleOwner = viewLifecycleOwner
        bindings.viewModel = viewModel
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareForLoading()

        bindings.motionFg.setTransitionListener(object : MotionLayout.TransitionListener {

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
                    bindings.motionBg.setTransition(R.id.setBegin, R.id.setHalf)
                    bindings.motionBg.progress = progress
                    Handler(Looper.getMainLooper()).post {
                        navigationBarColorAnimation.setCurrentFraction(progress)
                    }
                } else if (startId == R.id.setHalf && endId == R.id.setEnd) {
                    bindings.motionBg.setTransition(R.id.setHalf, R.id.setEnd)
                    bindings.motionBg.progress = progress
                }
            }

            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
                if (lastCompletedConstraint != null) {
                    when {
                        (currentId == R.id.setBegin && (lastCompletedConstraint == R.id.setBegin || lastCompletedConstraint == R.id.setHalf)) -> {
                            bindings.motionBg.progress = 0.0f
                            navigationBarColorAnimation.setCurrentFraction(0.0f)
                        }
                        (currentId == R.id.setHalf && lastCompletedConstraint == R.id.setBegin) -> {
                            bindings.motionBg.progress = 1.0f
                            navigationBarColorAnimation.setCurrentFraction(1.0f)
                        }
                        (currentId == R.id.setHalf && lastCompletedConstraint == R.id.setEnd) -> {
                            bindings.motionBg.progress = 0.0f
                        }
                        (currentId == R.id.setEnd && (lastCompletedConstraint == R.id.setEnd || lastCompletedConstraint == R.id.setHalf)) -> {
                            bindings.motionBg.progress = 1.0f
                        }
                    }
                }
                lastCompletedConstraint = currentId
            }

        })

        lifecycleScope.launchWhenResumed {
            viewModel.workingFlow.collect { isWorking ->
                Timber.i("Working changed to $isWorking")
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.messageFlow.collect { message ->
                when(message.type) {
                    Message.Type.MESSAGE -> {

                    }
                    Message.Type.ERROR -> {
                        if (bindings.motionFg.currentState == R.id.setLoading) {
                            bindings.txvError.text = message.data
                            bindings.txvError.visibility = View.VISIBLE
                            bindings.btnTryAgain.visibility = View.VISIBLE
                        } else {
                            Toasty.custom(
                                requireContext(),
                                message.data!!,
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
                    }
                    Message.Type.READY -> {
                        updateNavigationBarColor(false, true)
                        if (bindings.motionFg.currentState == R.id.setLoading) {
                            bindings.txvError.visibility = View.INVISIBLE
                            bindings.btnTryAgain.visibility = View.INVISIBLE
                            bindings.motionFg.transitionToEnd()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.useMetricSystem()
                .drop(1)
                .catch { ex ->
                    Timber.e(ex)
                }
                .collect { _ ->
                    Timber.i("Use metric system flow collector")
                    viewModel.resetScheduleWeatherInfo()
                }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.isDarkModeOn()
                .catch { ex->
                    Timber.e(ex)
                }
                .collect { isDarkModeOn ->
                    AppCompatDelegate.setDefaultNightMode(if (isDarkModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                }
        }

        bindings.btnAdd.setOnClickListener {
            MoreOptionsPopup.showDialog(
                parentFragmentManager
            ) { _, data ->
                val option = if (data.containsKey(MoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)) {
                    data.getInt(MoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION)
                } else {
                    -1
                }
                when (option) {
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_METRIC_SYSTEM_UPDATED -> {
                        Timber.i("Metric system configuration has changed. Flow collector will handle it")
                    }
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_DARK_MODE_UPDATED -> {
                        Timber.i("Dark mode configuration has changed. Flow collector will handle it")
                    }
                    MoreOptionsPopup.MORE_OPTIONS_POPUP_OPEN_ABOUT_VIEW -> {
                        Timber.i("Open About view request received")
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCommonActivity())
                    }
                }
            }
        }

        bindings.btnTryAgain.setOnClickListener {
            startWeatherMonitor()
        }

        startWeatherMonitor()
    }

    private fun startWeatherMonitor() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    lifecycleScope.launchWhenResumed {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                viewModel.startWeatherMonitor()
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
                Toasty.custom(
                    requireContext(),
                    it.name,
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
            .check()
    }

    private fun prepareForLoading() {
        val lastAnimation = LottieAnimationViewBindings.weatherAnimation(
            viewModel.lastConditionCode(),
            viewModel.lastWasDayLight(),
            viewModel.lastWasThereVisibility()
        )
        bindings.lotConditionImage.tag = lastAnimation
        bindings.lotConditionImage.setAnimation(lastAnimation)
        bindings.lotConditionImage.speed = 1.0f
        bindings.lotConditionImage.repeatCount = LottieDrawable.INFINITE
        bindings.lotConditionImage.repeatMode = LottieDrawable.RESTART
        bindings.lotConditionImage.playAnimation()
        val lastBackground = ConstraintLayoutBindings.conditionBackground(
            viewModel.lastConditionCode(),
            viewModel.lastWasDayLight(),
            viewModel.lastTemperature(),
            viewModel.useMetricSystemValue()
        )
        bindings.rootLayout.setBackgroundResource(lastBackground)
        updateNavigationBarColor()
    }

    private fun updateNavigationBarColor(useSurface: Boolean = true, animate: Boolean = false) {
        val previousBottomColor = lastConditionBottomColor
        lastConditionBottomColor = ConstraintLayoutBindings.conditionBottomColor(
            viewModel.lastConditionCode(),
            viewModel.lastWasDayLight(),
            viewModel.lastTemperature(),
            viewModel.useMetricSystemValue()
        )
        if (bindings.motionFg.currentState in arrayOf(R.id.setLoading, R.id.setBegin)) {
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopMonitors()
    }
}