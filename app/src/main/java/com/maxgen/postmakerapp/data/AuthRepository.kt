package com.maxgen.postmakerapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maxgen.postmakerapp.model.UserModel

class AuthRepository {

    fun createUser(user: UserModel): LiveData<String> {
        val authResponse = MutableLiveData<String>()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.email, user.pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    FirebaseFirestore.getInstance().collection(UserModel.UserEnum.USER.name)
                        .document(user.email)
                        .set(user).addOnCompleteListener { store ->
                            if (store.isSuccessful) {
                                authResponse.value = "Registered Successfully"
                            } else authResponse.value = store.exception.toString()
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

    companion object {
        private const val TAG = "AuthRepository"
    }
}