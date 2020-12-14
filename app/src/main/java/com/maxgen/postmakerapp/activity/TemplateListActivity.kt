package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.maxgen.postmakerapp.R
import kotlinx.android.synthetic.main.activity_template_list.*

class TemplateListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        cv_fixed.setOnClickListener {
            startActivity(Intent(this, Template1Activity::class.java))
        }

        cv_logo.setOnClickListener {
            startActivity(Intent(this, Template2Activity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}