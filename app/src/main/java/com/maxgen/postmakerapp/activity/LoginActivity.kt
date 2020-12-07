package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maxgen.postmakerapp.adapter.OnLoginListener
import com.maxgen.postmakerapp.databinding.ActivityLoginBinding
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity(), OnLoginListener {
    private lateinit var viewBinding: ActivityLoginBinding
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        preferences = getSharedPreferences(UserModel.UserEnum.USER.name, MODE_PRIVATE)

        val viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        viewBinding.viewModel = viewModel

        viewModel.loginListener = this
    }

    override fun onUserLogin(loginResponse: LiveData<UserModel>?) {
        loginResponse?.observe(this, Observer {
            SharedPreferenceUser.getInstance().loginUser(it, this)
            if (SharedPreferenceUser.getInstance().getUser(this).email.isNotEmpty()) {
                startActivity(Intent(this, SelectPostTypeActivity::class.java))
                finish()
            }
        })
    }

    override fun goToRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }
}