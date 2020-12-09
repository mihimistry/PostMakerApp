package com.maxgen.postmakerapp.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.databinding.ActivitySelectPostTypeBinding


class SelectPostTypeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySelectPostTypeBinding
    private var adView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectPostTypeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        loadAd()
        setupUI()

    }

    private fun loadAd() {
        MobileAds.initialize(this) {}
        adView = LayoutInflater.from(this).inflate(R.layout.ad_layout, null)
        val mAdView = adView?.findViewById<AdView>(R.id.mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView?.loadAd(adRequest)
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
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setView(adView)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id -> super.onBackPressed() })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }
}