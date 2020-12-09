package com.maxgen.postmakerapp.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.maxgen.postmakerapp.adapter.OnImageClickListener
import com.maxgen.postmakerapp.data.UserRepository
import com.maxgen.postmakerapp.model.UserModel

class UserViewModel : ViewModel() {

    var imageListener: OnImageClickListener? = null
    fun getUserDetails(email: String): LiveData<UserModel> {
        return UserRepository().getUserDetails(email)
    }

    private fun changeImage(context: Context) {
        val options = arrayOf<CharSequence>("from Camera", "from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Background Image")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "from Camera" -> {
                    imageListener?.getImageFromCamera()
                }

                options[item] == "from Gallery" -> {
                    imageListener?.getImageFromGallery()
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun uploadImage(imageUri: Uri?, email: String) {
        UserRepository().uploadImageToFirebase(imageUri, email)
    }

    fun profileImage(context: Context) {
        val options = arrayOf<CharSequence>("Choose/Change Image", "Remove Image", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Profile Image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Choose/Change Image" -> {
                    changeImage(context)
                }

                options[item] == "Remove Image" -> {
                    imageListener?.removeImage()
                }

                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    fun removeProfileImage(user: UserModel) {
        UserRepository().removeImage(user)
    }

    fun changeProfileImage(imageUrl: String, imageUri: Uri?, email: String) {
        UserRepository().updateImage(imageUrl, imageUri, email)

    }


}