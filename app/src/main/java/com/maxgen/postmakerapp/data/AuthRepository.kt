package com.maxgen.postmakerapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.model.UserModel.UserEnum

class AuthRepository {

    fun createUser(user: UserModel): LiveData<String> {
        val authResponse = MutableLiveData<String>()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseFirestore.getInstance().collection(UserModel.UserEnum.USER.name)
                        .document(user.email)
                        .set(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                authResponse.value = "Registered Successfully"
                            } else authResponse.value = task.exception.toString()
                        }
                } else authResponse.value = it.exception.toString()
            }
        return authResponse
    }

    fun loginUser(email: String?, pass: String?): LiveData<UserModel> {
        val loginResponse = MutableLiveData<UserModel>()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email!!, pass!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseFirestore.getInstance().collection(UserModel.UserEnum.USER.name)
                        .document(email)
                        .addSnapshotListener { value, error ->
                            if (value != null && value.exists()) {
                                val user = value.toObject(UserModel::class.java)
                                loginResponse.value = user
                            }
                            if (error != null) {
                                Log.e(TAG, "loginUser: ", error)
                            }
                        }
                } else Log.e(TAG, "loginUser: ", it.exception)
            }
        return loginResponse
    }

    fun updateUserProfile(
        userName: String,
        email: String,
        phone: String,
        website: String
    ): LiveData<String> {
        val updateResponse = MutableLiveData<String>()
        val map = HashMap<String, Any>()
        map[UserEnum.userName.name] = userName
        map[UserEnum.phone.name] = phone
        map[UserEnum.website.name] = website

        FirebaseFirestore.getInstance().collection(UserEnum.USER.name).document(email)
            .update(map).addOnCompleteListener {
                if (it.isSuccessful)
                    updateResponse.value = "Updated Successfully"
                else {
                    Log.e(TAG, "updateUserProfile: ", it.exception)
                    updateResponse.value = "failed"
                }
            }
        return updateResponse
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}