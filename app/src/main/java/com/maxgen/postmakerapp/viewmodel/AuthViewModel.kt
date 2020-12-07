package com.maxgen.postmakerapp.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maxgen.postmakerapp.adapter.OnLoginListener
import com.maxgen.postmakerapp.adapter.OnRegistrationListener
import com.maxgen.postmakerapp.data.AuthRepository
import com.maxgen.postmakerapp.model.UserModel

class AuthViewModel : ViewModel() {
    var userName: String = ""
    var email: String = ""
    var pass: String = ""
    var phone: String = ""
    var website: String = ""
    var registrationListener: OnRegistrationListener? = null
    var loginListener: OnLoginListener? = null
    var signUpResponse: LiveData<String>? = null
    var loginResponse: LiveData<UserModel>? = null

    fun onRegistrationClicked(view: View) {
        val user = UserModel(userName, email, pass, phone, website)
        signUpResponse = AuthRepository().createUser(user)
        registrationListener?.onUserRegister(signUpResponse!!)
    }

    fun onGoToLoginClicked(view: View) {
        registrationListener?.goToLogin()
    }

    fun onLoginClicked(view: View) {
        loginResponse = AuthRepository().loginUser(email, pass)
        loginListener?.onUserLogin(loginResponse)
    }

    fun onGoToRegistrationClicked(view: View) {
        loginListener?.goToRegistration()
    }
}