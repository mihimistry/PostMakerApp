package com.maxgen.postmakerapp.adapter

import androidx.lifecycle.LiveData
import com.maxgen.postmakerapp.model.UserModel

interface OnLoginListener {
    fun onUserLogin(loginResponse: LiveData<UserModel>?)
    fun goToRegistration()
}