package com.maxgen.postmakerapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maxgen.postmakerapp.databinding.FragmentTextEditorBinding

class TextEditorFragment : Fragment() {

    private lateinit var binding: FragmentTextEditorBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTextEditorBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}