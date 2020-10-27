package org.xapps.apps.weatherx.views.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentHomeBinding
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.viewmodels.HomeViewModel
import org.xapps.apps.weatherx.views.adapters.DailyInfoSimpleAdapter
import org.xapps.apps.weatherx.views.adapters.HourlyAdapter
import org.xapps.apps.weatherx.views.adapters.HourlySimpleAdapter
import org.xapps.apps.weatherx.views.binding.ConstraintLayoutBindings
import org.xapps.apps.weatherx.views.binding.LottieAnimationViewBindings
import org.xapps.apps.weatherx.views.popups.MoreOptionsPopup
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var settings: SettingsService

    private var lastCompletedConstraint: Int? = null

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

        motionFg.setTransitionListener(object: MotionLayout.TransitionListener {

            override fun onTransitionTrigger(layout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}

            override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {}

            override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if(startId == R.id.setBegin && endId == R.id.setHalf) {
                    motionBg.setTransition(R.id.setBegin, R.id.setHalf)
                    motionBg.progress = progress
                } else if (startId == R.id.setHalf && endId == R.id.setEnd) {
                    motionBg.setTransition(R.id.setHalf, R.id.setEnd)
                    motionBg.progress = progress
                }
            }

            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
                if(lastCompletedConstraint != null) {
                    when {
                        (currentId == R.id.setBegin && (lastCompletedConstraint == R.id.setBegin || lastCompletedConstraint == R.id.setHalf)) -> {
                            motionBg.progress = 0.0f
                        }
                        (currentId == R.id.setHalf && lastCompletedConstraint == R.id.setBegin) -> {
                            motionBg.progress = 1.0f
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


        listHourlySimple.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        listHourlySimple.adapter = HourlySimpleAdapter(viewModel.hourlyWeather)

        listHourly.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        listHourly.adapter = HourlyAdapter(viewModel.hourlyWeather)

        listDaily.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        listDaily.adapter = DailyInfoSimpleAdapter(viewModel.dailyWeather)


        viewModel.watchWorking().observe(viewLifecycleOwner, Observer { isWorking ->

        })

        viewModel.watchError().observe(viewLifecycleOwner, Observer { errorMessage ->

        })

        viewModel.watchConditionGroup().observe(viewLifecycleOwner, Observer { group ->

        })

        viewModel.watchReady().observe(viewLifecycleOwner, Observer { isReady ->
            if(isReady) {
                motionFg.transitionToEnd()
            } else {
                // Get back to the loading view
            }
        })

        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            viewModel.prepareMonitoring()
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
            moreOptionPopup.setTargetFragment(this@HomeFragment, MoreOptionsPopup.MORE_OPTIONS_POPUP_CODE)
            val fragmentManager = parentFragmentManager.beginTransaction()
            moreOptionPopup.show(fragmentManager, "MoreOptionsPopup")
        }
    }

    fun prepareForLoading() {
        val lastAnimation = LottieAnimationViewBindings.weatherAnimation(viewModel.lastConditionCode(), viewModel.lastWasDayLight(), viewModel.lastWasThereVisibility())
        lotConditionImage.tag = lastAnimation
        lotConditionImage.setAnimation(lastAnimation)
        lotConditionImage.speed = 1.0f
        lotConditionImage.repeatCount = LottieDrawable.INFINITE
        lotConditionImage.repeatMode = LottieDrawable.RESTART
        lotConditionImage.playAnimation()
        val lastBackground = ConstraintLayoutBindings.conditionBackground(viewModel.lastConditionCode(), viewModel.lastWasDayLight(), viewModel.lastTemperature(), viewModel.useMetric())
        rootLayout.setBackgroundResource(lastBackground)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == MoreOptionsPopup.MORE_OPTIONS_POPUP_CODE) {
            if(resultCode == MoreOptionsPopup.MORE_OPTIONS_POPUP_ACCEPTED_CODE) {
                val option = data?.getIntExtra(MoreOptionsPopup.MORE_OPTIONS_POPUP_OPTION, -1) ?: -1
                when (option) {
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
}