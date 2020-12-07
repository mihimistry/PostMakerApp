package com.maxgen.postmakerapp.utils

import android.content.Context
import com.maxgen.postmakerapp.model.UserModel


class SharedPreferenceUser private constructor() {
    companion object {
        private var mInstance: SharedPreferenceUser? = null

        fun getInstance(): SharedPreferenceUser {
            if (mInstance == null) mInstance = SharedPreferenceUser()
            return mInstance as SharedPreferenceUser
        }
    }

    fun loginUser(user: UserModel, context: Context) {
        val preferenceUser =
            context.getSharedPreferences(UserModel.UserEnum.USER.name, Context.MODE_PRIVATE)
        val editor = preferenceUser.edit()
        editor.putString(UserModel.UserEnum.userName.name, user.userName)
        editor.putString(UserModel.UserEnum.email.name, user.email)
        editor.putString(UserModel.UserEnum.phone.name, user.phone)
        editor.putString(UserModel.UserEnum.website.name, user.website)
        editor.apply()
    }

    fun getUser(context: Context): UserModel {
        val userPreferences =
            context.getSharedPreferences(UserModel.UserEnum.USER.name, Context.MODE_PRIVATE)

        return UserModel(
            userPreferences.getString(UserModel.UserEnum.userName.name, "")!!,
            userPreferences.getString(UserModel.UserEnum.email.name, "")!!,
            userPreferences.getString(UserModel.UserEnum.phone.name, "")!!,
            userPreferences.getString(UserModel.UserEnum.website.name, "")!!
        )
    }

}
