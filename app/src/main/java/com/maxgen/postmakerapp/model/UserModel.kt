package com.maxgen.postmakerapp.model

class UserModel(
    val userName: String = "",
    val email: String = "",
    val pass: String = "",
    val phone: String = "",
    val website: String = "",
    val imageUrl: String = ""
) {
    enum class UserEnum {
        USER,
        userName,
        email,
        pass,
        phone,
        website,
        imageUrl
    }
}