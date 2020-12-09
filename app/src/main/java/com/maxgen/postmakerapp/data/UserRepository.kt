package com.maxgen.postmakerapp.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    fun uploadImageToFirebase(imageUri: Uri?, email: String) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("images/image" + System.currentTimeMillis())

        storageRef.putFile(imageUri!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                val map = HashMap<String, Any>()
                map[UserModel.UserEnum.imageUrl.name] = it.toString()
                FirebaseFirestore.getInstance().collection("USER")
                    .document(email).update(map)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "uploadImageToFirebase: Image Uploaded Successfully")
                        } else {
                            Log.e(TAG, "uploadImageToFirebase: ", task.exception)
                        }
                    }

            }
        }.addOnFailureListener {
            Log.e(TAG, "uploadImageToFirebase: ", it)
        }
            .addOnProgressListener {
                Log.d(TAG, "uploadImageToFirebase: Uploading..." )
            }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}