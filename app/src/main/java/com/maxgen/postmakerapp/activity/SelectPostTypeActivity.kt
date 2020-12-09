package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.databinding.ActivitySelectPostTypeBinding


class SelectPostTypeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySelectPostTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectPostTypeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        MobileAds.initialize(this) {}
        setupUI()

    }

    private fun setupUI() {
        viewBinding.cvProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        viewBinding.cvTemplate.setOnClickListener {
            startActivity(Intent(this, TemplateListActivity::class.java))
        }

        viewBinding.cvCreate.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ExitActivity::class.java))
    }
}