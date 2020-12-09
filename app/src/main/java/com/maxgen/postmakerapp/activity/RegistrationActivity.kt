package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.maxgen.postmakerapp.databinding.ActivityRegistrationBinding
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.utils.MyUtils.getEDTText
import com.maxgen.postmakerapp.utils.MyUtils.setEDTError
import com.maxgen.postmakerapp.utils.MyUtils.toString
import com.maxgen.postmakerapp.viewmodel.AuthViewModel

class RegistrationActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegistrationBinding
    private val userName get() = getEDTText(viewBinding.edtUsername)
    private val email get() = getEDTText(viewBinding.edtEmail)
    private val pass get() = getEDTText(viewBinding.edtPass)
    private val cPass get() = getEDTText(viewBinding.edtCpass)
    private val phone get() = getEDTText(viewBinding.edtMobile)
    private var website = ""
    private var viewModel: AuthViewModel? = null
    private var signUpResponse: LiveData<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        viewBinding.viewModel = viewModel

        viewBinding.btnRegister.setOnClickListener {
            validateAndCreateUser()
        }

        viewBinding.tvRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateAndCreateUser() {
        var flag = true
        website = viewBinding.edtWebsite.text.toString()
        if (userName.isEmpty()) {
            setEDTError(viewBinding.edtUsername, "Please enter valid first name.")
            flag = false
        }
        if (pass.length < 6) {
            setEDTError(viewBinding.edtPass, "Password must at least 6 character long.")
            flag = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setEDTError(viewBinding.edtEmail, "Please enter valid email.")
            flag = false
        }
        if (!Patterns.PHONE.matcher(phone).matches() || phone.length != 10) {
            setEDTError(viewBinding.edtMobile, "Please enter valid mobile number.")
            flag = false
        }
        if (pass != cPass) {
            setEDTError(viewBinding.edtCpass, "Password does not match.")
            flag = false
        }

        if (flag) {
            checkAndCreateUser()
        }
    }

    private fun checkAndCreateUser() {
        if (website.isNotEmpty() && website.endsWith("/")) {
            website = website.substring(0, website.length - 1);
        }
        val user = UserModel(userName, email, pass, phone, website)
        signUpResponse = viewModel?.registerUser(user)
        signUpResponse?.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

}