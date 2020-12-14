package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maxgen.postmakerapp.adapter.OnLoginListener
import com.maxgen.postmakerapp.databinding.ActivityLoginBinding
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.utils.MyUtils
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity(), OnLoginListener {
    private lateinit var viewBinding: ActivityLoginBinding
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var viewModel: AuthViewModel? = null
    private var loginResponse: LiveData<UserModel>? = null
    private val email get() = MyUtils.getEDTText(viewBinding.edtEmail)
    private val pass get() = MyUtils.getEDTText(viewBinding.edtPass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        preferences = getSharedPreferences(UserModel.UserEnum.USER.name, MODE_PRIVATE)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        viewBinding.viewModel = viewModel

        viewModel?.loginListener = this
        if (preferences!!.contains(UserModel.UserEnum.email.name)) {
            startActivity(Intent(this, SelectPostTypeActivity::class.java))
            finish()
        }

        viewBinding.btnLogin.setOnClickListener {
            validateAndSignInUser()
        }

    }

    private fun validateAndSignInUser() {
        var flag = true

        if (pass.length < 6) {
            MyUtils.setEDTError(viewBinding.edtPass, "Password must at least 6 character long.")
            flag = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MyUtils.setEDTError(viewBinding.edtEmail, "Please enter valid email.")
            flag = false
        }

        if (flag) {
            checkAndSignInUser()
        }
    }

    private fun checkAndSignInUser() {
        loginResponse = viewModel?.userLogin(email, pass)
        loginResponse?.observe(this, Observer {
            if (it != null) {
                SharedPreferenceUser.getInstance().loginUser(it, this)
                if (SharedPreferenceUser.getInstance().getUser(this).email.isNotEmpty()) {
                    startActivity(Intent(this, SelectPostTypeActivity::class.java))
                    finish()
                }
            } else Toast.makeText(this, "Enter Valid Email Id/Password", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onUserLogin(loginResponse: LiveData<UserModel>?) {

    }

    override fun goToRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }
}