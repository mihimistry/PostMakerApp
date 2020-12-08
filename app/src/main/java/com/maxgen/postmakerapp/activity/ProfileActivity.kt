package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.activity.CreatePostActivity.Companion.GET_FROM_CAMERA
import com.maxgen.postmakerapp.activity.CreatePostActivity.Companion.GET_FROM_GALLERY
import com.maxgen.postmakerapp.adapter.OnImageClickListener
import com.maxgen.postmakerapp.databinding.ActivityProfileBinding
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.UserViewModel
import java.io.FileNotFoundException
import java.io.IOException

class ProfileActivity : AppCompatActivity(), OnImageClickListener {
    private lateinit var viewBinding: ActivityProfileBinding
    private var userDetails: LiveData<UserModel>? = null
    private var viewModel: UserViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        setupUI()

    }

    private fun setupUI() {
        viewModel?.imageListener = this
        userDetails =
            viewModel?.getUserDetails(SharedPreferenceUser.getInstance().getUser(this).email)

        userDetails?.observe(this, Observer {
            viewBinding.user = it
            Glide.with(this).load(it.imageUrl).into(viewBinding.profileImage)
        })


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun getImageFromCamera() {

        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, GET_FROM_CAMERA)
    }

    override fun getImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_FROM_GALLERY)
    }

    override fun removeImage() {
        Toast.makeText(this, "remove Image", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {

                GET_FROM_CAMERA -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    viewBinding.profileImage.setImageBitmap(selectedImage)
                    uploadToStorage(data.data)
                }
                GET_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    var bitmap: Bitmap? = null
                    try {

                        bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        viewBinding.profileImage.setImageBitmap(bitmap)
                        uploadToStorage(data.data)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun uploadToStorage(imageUri: Uri?) {
        viewModel?.uploadImage(imageUri, SharedPreferenceUser.getInstance().getUser(this).email)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut()
            SharedPreferenceUser.getInstance().logoutUser(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}