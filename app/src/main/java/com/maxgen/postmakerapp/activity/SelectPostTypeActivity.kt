package com.maxgen.postmakerapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maxgen.postmakerapp.databinding.ActivitySelectPostTypeBinding

class SelectPostTypeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySelectPostTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivitySelectPostTypeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)


    }
}