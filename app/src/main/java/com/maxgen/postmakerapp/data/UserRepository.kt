package com.maxgen.postmakerapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.maxgen.postmakerapp.model.UserModel

class UserRepository {

    fun getUserDetails(email: String): LiveData<UserModel> {
        val userDetails = MutableLiveData<UserModel>()

        FirebaseFirestore.getInstance().collection(UserModel.UserEnum.USER.name)
            .document(email)
            .addSnapshotListener { value, error ->
                if (value != null && value.exists()) {
                    userDetails.value = value.toObject(UserModel::class.java)
                }
                if (error != null) {
                    Log.e(TAG, "getUserDetails: ", error)
                }
            }
        return userDetails
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}