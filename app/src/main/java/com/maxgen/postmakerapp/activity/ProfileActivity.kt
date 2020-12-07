package com.maxgen.postmakerapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maxgen.postmakerapp.databinding.ActivityProfileBinding
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.UserViewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityProfileBinding
    private var userDetails: LiveData<UserModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userDetails =
            viewModel.getUserDetails(SharedPreferenceUser.getInstance().getUser(this).email)

        userDetails?.observe(this, Observer {
            viewBinding.user = it
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}