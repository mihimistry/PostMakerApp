package com.maxgen.postmakerapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.maxgen.postmakerapp.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cv_profile.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        cv_create.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreatePostFragment2())
        }

        cv_template.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTemplateListFragment())
        }
    }
}