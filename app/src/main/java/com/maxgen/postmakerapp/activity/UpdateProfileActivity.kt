package com.maxgen.postmakerapp.activity

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.maxgen.postmakerapp.databinding.ActivityUpdateProfileBinding
import com.maxgen.postmakerapp.utils.MyUtils
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.AuthViewModel

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityUpdateProfileBinding
    private var viewModel: AuthViewModel? = null
    private var updateResponse: LiveData<String>? = null
    private val userName get() = MyUtils.getEDTText(viewBinding.edtUsername)
    private val email get() = MyUtils.getEDTText(viewBinding.edtEmail)
    private val phone get() = MyUtils.getEDTText(viewBinding.edtMobile)
    private val website get() = MyUtils.getEDTText(viewBinding.edtWebsite)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adRequest = AdRequest.Builder().build()
        viewBinding.adView.loadAd(adRequest)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        viewBinding.user = SharedPreferenceUser.getInstance().getUser(this)

        viewBinding.btnUpdate.setOnClickListener { validateAndUpdateProfile() }

    }

    private fun validateAndUpdateProfile() {
        var flag = true
        if (userName.isEmpty()) {
            MyUtils.setEDTError(viewBinding.edtUsername, "Please enter valid first name.")
            flag = false
        }
        if (!Patterns.PHONE.matcher(phone).matches() || phone.length != 10) {
            MyUtils.setEDTError(viewBinding.edtMobile, "Please enter valid mobile number.")
            flag = false
        }
        if (flag) {
            updateProfile()
        }
    }

    private fun updateProfile() {
        updateResponse = viewModel?.updateUserProfile(userName, email, phone, website)
        updateResponse?.observe(this, Observer {
            if (it == "failed")
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}