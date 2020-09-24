package org.xapps.apps.weatherx.views.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentHomeBinding
import org.xapps.apps.weatherx.services.models.HourlyInfo
import org.xapps.apps.weatherx.services.models.Location
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.viewmodels.HomeViewModel
import org.xapps.apps.weatherx.views.adapters.HourlyInfoSimpleAdapter
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var settings: SettingsService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedElementEnterTransition =
            TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)
        sharedElementReturnTransition =
            TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        ViewCompat.setTranslationZ(binding.root, 1f)
        binding.location = Location("Serbia", "Stari", 0.0, 0.0, 10240, true, 28, "Clear")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.i("Checking settings var =  ${settings}")

        motionFg.setTransitionListener(object: MotionLayout.TransitionListener {

            override fun onTransitionTrigger(layout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}

            override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
                if(startId == R.id.setLoading) {
                    btnAdd.visibility = View.VISIBLE
                }
            }

            override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if(startId == R.id.setBegin && endId == R.id.setHalf) {
                    motionBg.setTransition(R.id.setBegin, R.id.setHalf)
                    motionBg.progress = progress
                } else if (startId == R.id.setHalf && endId == R.id.setEnd) {
                    motionBg.setTransition(R.id.setHalf, R.id.setEnd)
                    motionBg.progress = progress
                }
            }

            override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {}

        })

        listHourly.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        listHourly.adapter = HourlyInfoSimpleAdapter(listOf(
            HourlyInfo(1),
            HourlyInfo(1),
            HourlyInfo(1),
            HourlyInfo(1),
            HourlyInfo(1),
            HourlyInfo(1)
        ))

        Handler().postDelayed({
            motionFg.transitionToEnd()
        }, 2000)
    }

}