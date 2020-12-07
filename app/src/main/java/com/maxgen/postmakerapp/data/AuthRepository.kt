package com.maxgen.postmakerapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maxgen.postmakerapp.model.UserModel

class AuthRepository {

    fun createUser(user: UserModel): LiveData<String> {
        var authResponse = MutableLiveData<String>()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    authResponse = storeUser(user)
                } else authResponse.value = it.exception.toString()
            }
        return authResponse
    }

    private fun storeUser(user: UserModel): MutableLiveData<String> {
        val registerResponse = MutableLiveData<String>()
        FirebaseFirestore.getInstance().collection(UserModel.UserEnum.USER.name)
            .document(user.email)
            .set(user).addOnCompleteListener {
                if (it.isSuccessful) {
                    registerResponse.value = "Registered Successfully"
                } else registerResponse.value = it.exception.toString()
            }
        return registerResponse
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
                }
                else Log.e(TAG, "loginUser: ",it.exception )
            }
        return loginResponse
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}