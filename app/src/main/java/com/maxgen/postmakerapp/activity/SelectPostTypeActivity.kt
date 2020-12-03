package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.maxgen.postmakerapp.databinding.ActivitySelectPostTypeBinding

class SelectPostTypeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySelectPostTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectPostTypeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        MobileAds.initialize(this) {}
        viewBinding.cvTemplate.setOnClickListener {
            startActivity(Intent(this, Template1Activity::class.java))
        }

        viewBinding.cvCreate.setOnClickListener {
            startActivity(Intent(this, PostFormat1Activity::class.java))
        }
    }
}