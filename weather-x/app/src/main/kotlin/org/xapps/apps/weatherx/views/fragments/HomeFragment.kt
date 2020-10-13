package org.xapps.apps.weatherx.views.fragments

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentHomeBinding
import org.xapps.apps.weatherx.services.models.Place
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.viewmodels.HomeViewModel
import org.xapps.apps.weatherx.views.adapters.DailyInfoSimpleAdapter
import org.xapps.apps.weatherx.views.adapters.HourlyAdapter
import org.xapps.apps.weatherx.views.adapters.HourlySimpleAdapter
import timber.log.Timber
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
        ViewCompat.setTranslationZ(binding.root, 1f)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        listHourlySimple.adapter = HourlySimpleAdapter(viewModel.hourlyList)

        listHourly.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        listHourly.adapter = HourlyAdapter(viewModel.hourlyList)

        listDaily.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        listDaily.adapter = DailyInfoSimpleAdapter(viewModel.dailyList)


        viewModel.watchWorking().observe(viewLifecycleOwner, Observer { isWorking ->

        })

        viewModel.watchError().observe(viewLifecycleOwner, Observer { errorMessage ->

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
                            Toast.makeText(requireContext(), "OK", Toast.LENGTH_LONG).show()

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
                Toast.makeText(requireContext(), "${it.name}", Toast.LENGTH_LONG).show()
            }
            .check()

        Handler().postDelayed({
            motionFg.transitionToEnd()
        }, 2000)
    }

}