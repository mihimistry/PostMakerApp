package com.maxgen.postmakerapp.adapter

import androidx.lifecycle.LiveData

interface OnRegistrationListener {
    fun onUserRegister(signUpResponse: LiveData<String>)
    fun goToLogin()
}