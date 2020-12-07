package com.maxgen.postmakerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maxgen.postmakerapp.data.UserRepository
import com.maxgen.postmakerapp.model.UserModel

class UserViewModel : ViewModel() {

    fun getUserDetails(email: String): LiveData<UserModel> {
        return UserRepository().getUserDetails(email)
    }
}