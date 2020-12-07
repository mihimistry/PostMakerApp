package com.maxgen.postmakerapp.model

import com.google.firebase.firestore.Exclude

class UserModel(
    val userName: String = "",
    val email: String = "",
    @Exclude
    val pass: String = "",
    val phone: String = "",
    val website: String = "",
) {
    enum class UserEnum {
        USER,
        userName,
        email,
        pass,
        phone,
        website
    }
}