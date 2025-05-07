package com.example.spaceflightnews.presentation.fragment.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.spaceflightnews.R
import com.example.spaceflightnews.databinding.FragmentSplashBinding
import com.example.spaceflightnews.util.BaseFragment


class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.animationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                navigateToHomeFragment()
            }
        })
    }

    private fun navigateToHomeFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
        findNavController().navigate(action.actionId, null, NavOptions.Builder()
            .setPopUpTo(R.id.splashFragment, true)
            .build())
    }
}