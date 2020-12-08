package com.maxgen.postmakerapp.viewmodel

import android.net.Uri
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maxgen.postmakerapp.adapter.OnImageClickListener
import com.maxgen.postmakerapp.data.UserRepository
import com.maxgen.postmakerapp.model.UserModel

class UserViewModel : ViewModel() {

    var imageListener: OnImageClickListener? = null
    fun getUserDetails(email: String): LiveData<UserModel> {
        return UserRepository().getUserDetails(email)
    }

    fun onImageClick(view: View) {
        val options = arrayOf<CharSequence>("Change Image", "Remove Image", "Cancel")
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Choose Background Image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Change Image" -> {
                    changeImage(view)
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

    private fun changeImage(view: View) {
        val options = arrayOf<CharSequence>("from Camera", "from Gallery", "Cancel")
        val builder = AlertDialog.Builder(view.context)
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


}