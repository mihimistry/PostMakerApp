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

    fun registerUser(user: UserModel): LiveData<String> {
        return AuthRepository().createUser(user)
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

    fun userLogin(email: String, pass: String): LiveData<UserModel> {
        return AuthRepository().loginUser(email, pass)
    }

    fun updateUserProfile(
        userName: String,
        email: String,
        phone: String,
        website: String
    ): LiveData<String> {
        return AuthRepository().updateUserProfile(userName, email, phone, website)
    }
}