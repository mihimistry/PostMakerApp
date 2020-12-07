package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maxgen.postmakerapp.adapter.OnRegistrationListener
import com.maxgen.postmakerapp.databinding.ActivityRegistrationBinding
import com.maxgen.postmakerapp.viewmodel.AuthViewModel

class RegistrationActivity : AppCompatActivity(), OnRegistrationListener {
    private lateinit var viewBinding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        viewBinding.viewModel = viewModel
        viewModel.registrationListener = this
    }

    override fun onUserRegister(signUpResponse: LiveData<String>) {
        signUpResponse.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
    }

    override fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}