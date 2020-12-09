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
                Log.d(TAG, "uploadImageToFirebase: Uploading...")
            }
    }

    fun removeImage(user: UserModel) {
        FirebaseStorage.getInstance().getReferenceFromUrl(user.imageUrl).delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val map = HashMap<String, Any>()
                    map[UserModel.UserEnum.imageUrl.name] = ""
                    FirebaseFirestore.getInstance().collection("USER")
                        .whereEqualTo(UserModel.UserEnum.imageUrl.name, user.imageUrl)
                        .addSnapshotListener { value, error ->
                            if (value != null && !value.isEmpty) {
                                value.documents[0].reference.update(map)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "removeImage: Image Removed")
                                        } else Log.e(TAG, "removeImage: ", task.exception)
                                    }
                            }
                            if (error != null) {
                                Log.e(TAG, "removeProfileImage: ", error)
                            }
                        }
                }
            }
    }

    fun updateImage(imageUrl: String, imageUri: Uri?, email: String) {
        FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val storageRef =
                        FirebaseStorage.getInstance().reference.child("images/image" + System.currentTimeMillis())

                    storageRef.putFile(imageUri!!).addOnSuccessListener { uploadTask ->
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val map = HashMap<String, Any>()
                            map[UserModel.UserEnum.imageUrl.name] = uri.toString()
                            FirebaseFirestore.getInstance().collection("USER")
                                .document(email).update(map)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(
                                            TAG,
                                            "updateImage: Image Uploaded Successfully"
                                        )
                                    } else {
                                        Log.e(TAG, "updateImage: ", task.exception)
                                    }
                                }
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "updateImage: ", e)
                    }
                        .addOnProgressListener {
                            Log.d(TAG, "updateImage: Uploading...")
                        }
                }
            }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}