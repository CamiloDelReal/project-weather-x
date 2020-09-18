package org.xapps.apps.weatherx.views.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.AnimatedStateListDrawable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_splash.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.FragmentSplashBinding
import org.xapps.apps.weatherx.viewmodels.SplashViewModel
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    private val viewModel: SplashViewModel by viewModels()

    private var index = 1

    private val icons = listOf(
        "lottie-clear-sky-day.json",
        "lottie-clear-sky-night.json"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        ViewCompat.setTranslationZ(binding.root, 2f)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            val extras = FragmentNavigatorExtras(
                lotConditionImage to getString(R.string.transition_item)
            )
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment(), extras)
        }, 2000)

//        val geocoder = Geocoder(requireContext(), Locale.getDefault())
//        val f = geocoder.getFromLocation(0.0, 0.0, 1)

//        val g = AnimationDrawable()
//        g.addFrame(ContextCompat.getDrawable(requireContext(), R.drawable.ic_gradient_clear_sky_day)!!, 2000)
//        g.addFrame(ContextCompat.getDrawable(requireContext(), R.drawable.ic_gradient_clear_sky_night)!!, 2000)
//        g.setExitFadeDuration(1000)
//        g.callback
//        rootLayout.background = g
//        g.start()


//        val j = Timer().schedule(object: TimerTask() {
//            override fun run() {
//                Log.i("AppLogger", "Task here")
//                requireActivity().runOnUiThread {
//                    watch_animation.animate().alpha(0.0f).setDuration(250).setListener(object : AnimatorListenerAdapter() {
//                        override fun onAnimationEnd(animation: Animator) {
//                            watch_animation.alpha = 0.0f
//                            watch_animation.setAnimation(icons[index])
//                            watch_animation.speed = 1f
//                            watch_animation.playAnimation()
//                            index = (index + 1) % 2
//                            watch_animation.animate().alpha(1.0f).setDuration(250).setListener(object : AnimatorListenerAdapter() {
//                                override fun onAnimationEnd(animation: Animator) {
//                                    watch_animation.alpha = 1.0f
//                                }
//                            })
//                        }
//                    })
//                }
//            }
//        }, 2000, 2000)

//        btnChange.setOnClickListener {
////            watch_animation.setAnimation("weather-sunny.json")
////            watch_animation.speed = 1f
////            watch_animation.playAnimation()
////            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
////                requireActivity(),
////
////            )
//
//            // Launch second activity with Navigation Component API
//            val extras = FragmentNavigatorExtras(
//                watch_animation to getString(R.string.transition_item)
//            )
//
//            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment(), extras)
//        }
    }
}
