package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.databinding.ActivitySelectPostTypeBinding

class SelectPostTypeActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySelectPostTypeBinding
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectPostTypeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        viewBinding.adView.loadAd(adRequest)

        setupUI()
    }

    override fun onStart() {
        super.onStart()

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

    }

    private fun setupUI() {

        viewBinding.cvProfile.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                    }

                    override fun onAdOpened() {
                        // Code to be executed when the ad is displayed.
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    override fun onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    override fun onAdClosed() {
                        startActivity(
                            Intent(
                                this@SelectPostTypeActivity,
                                ProfileActivity::class.java
                            )
                        )

                        // Code to be executed when the interstitial ad is closed.
                    }
                }

            } else {
                startActivity(Intent(this, ProfileActivity::class.java))

                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }

        viewBinding.cvTemplate.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                    }

                    override fun onAdOpened() {
                        // Code to be executed when the ad is displayed.
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    override fun onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    override fun onAdClosed() {
                        startActivity(
                            Intent(
                                this@SelectPostTypeActivity,
                                TemplateListActivity::class.java
                            )
                        )

                        // Code to be executed when the interstitial ad is closed.
                    }
                }

            } else {
                startActivity(Intent(this@SelectPostTypeActivity, TemplateListActivity::class.java))

                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }

        viewBinding.cvCreate.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                    }

                    override fun onAdOpened() {
                        // Code to be executed when the ad is displayed.
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    override fun onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    override fun onAdClosed() {
                        // Code to be executed when the interstitial ad is closed.
                        startActivity(
                            Intent(
                                this@SelectPostTypeActivity,
                                CreatePostActivity::class.java
                            )
                        )
                    }
                }

            } else {
                startActivity(Intent(this, CreatePostActivity::class.java))

                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ExitActivity::class.java))
        finish()
    }


}