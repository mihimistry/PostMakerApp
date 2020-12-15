package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.maxgen.postmakerapp.databinding.ActivityExitBinding

class ExitActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityExitBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityExitBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        loadAd()
        setupUI()
    }

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        viewBinding.mAdView.loadAd(adRequest)
    }

    private fun setupUI() {

        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()

        viewBinding.btnNo.setOnClickListener {
            startActivity(Intent(this, SelectPostTypeActivity::class.java))
            finish()
        }

        viewBinding.btnYes.setOnClickListener {
            finishAffinity()
        }

    }
}