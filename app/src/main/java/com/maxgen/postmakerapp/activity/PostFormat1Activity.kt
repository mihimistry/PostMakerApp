package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.databinding.ActivityPostFormat1Binding
import com.maxgen.postmakerapp.multiTouchLib.MultiTouchListener
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class PostFormat1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityPostFormat1Binding

    private var image: ImageView? = null
    private var textView: View? = null
    private var imageView: View? = null
    private var removeText: ImageView? = null
    private var removeImage: ImageView? = null
    private var editText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostFormat1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addImage.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->

                when {
                    options[item] == "Take Photo" -> {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, GET_FROM_CAMERA)
                    }

                    options[item] == "Choose from Gallery" -> {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            GET_FROM_GALLERY
                        )
                    }

                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }

            builder.show()
        }

        binding.imgEditWeb.setOnClickListener {
            binding.llMain.visibility = View.GONE
            binding.llTextEdit.visibility = View.VISIBLE
            binding.imgTextClose.visibility = View.VISIBLE
            binding.imgEdit.visibility = View.GONE
        }
        binding.imgMain.setOnClickListener {

        }
        binding.imgEdit.setOnClickListener {
            binding.llMain.visibility = View.GONE
            binding.llTextEdit.visibility = View.VISIBLE
            binding.imgTextClose.visibility = View.VISIBLE
            binding.imgEdit.visibility = View.GONE
        }

        binding.imgDone.setOnClickListener {
            binding.llMain.visibility = View.VISIBLE
            binding.llTextEdit.visibility = View.GONE
            binding.imgTextClose.visibility = View.GONE
            binding.imgEdit.visibility = View.VISIBLE
        }

        binding.imgTextClose.setOnClickListener {
            binding.edtMain.setText("")
        }
        binding.imgTxtColor.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("SELECT", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        if (binding.edtMain.isFocused)
                            binding.edtMain.setTextColor(envelope!!.color)
                        if (binding.edtWeb.isFocused)
                            binding.edtWeb.setTextColor(envelope!!.color)
                    }
                })
                .setNegativeButton(
                    "CANCEL"
                ) { dialog, _ -> dialog?.dismiss(); }
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();
        }
        //  checkItemAvailability()

        binding.addWebsite.setOnClickListener {
            binding.webLayout.visibility = View.VISIBLE
            binding.webLayout.setOnTouchListener(MultiTouchListener())
        }

        binding.addText.setOnClickListener {

            binding.edtLayout.visibility = View.VISIBLE

            binding.edtLayout.setOnTouchListener(MultiTouchListener())

            /*
            textView = layoutInflater.inflate(R.layout.frame_text_view, binding.fotoBox, false)
            binding.fotoBox.addView(textView)

            removeText = textView?.findViewById(R.id.imgTextClose)
            editText = textView?.findViewById(R.id.textbubble)
            val frame: FrameLayout? = textView?.findViewById(R.id.text_layout)

            textView!!.setOnTouchListener(MultiTouchListener())

            removeText?.setOnClickListener {
                binding.fotoBox.removeView(frame)
            }

             */

        }

        binding.addLogo.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->

                when {
                    options[item] == "Take Photo" -> {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, GET_LOGO_FROM_CAMERA)
                    }

                    options[item] == "Choose from Gallery" -> {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            GET_LOGO_FROM_GALLERY
                        )
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        binding.deleteItem.setOnClickListener {
            binding.imgMain.setImageBitmap(null)
            binding.imgLogo.setImageBitmap(null)
            binding.edtLayout.visibility = View.GONE
        }

        binding.imgTxtStyle.setOnClickListener {
            when {
                binding.edtMain.typeface == Typeface.DEFAULT -> {
                    binding.edtMain.setTypeface(binding.edtMain.typeface, Typeface.BOLD)
                }
                binding.edtMain.typeface.isBold -> {
                    binding.edtMain.setTypeface(binding.edtMain.typeface, Typeface.ITALIC)
                }
                binding.edtMain.typeface.isItalic -> {
                    binding.edtMain.typeface == Typeface.DEFAULT
                }
            }
        }

        /*
        binding.edtMain.setOnLongClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Remove Selected Text?")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                binding.edtMain.setText("")
                binding.edtMain.visibility = View.GONE
                dialog.dismiss()
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            builder.show()
            return@setOnLongClickListener true
        }
         */

    }


//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        if (editMode) {
//            menu?.getItem(R.id.action_save)?.isVisible = false
//            menu?.getItem(R.id.action_share)?.isVisible = false
//            menu?.getItem(R.id.action_done)?.isVisible = true
//        }
//        return super.onPrepareOptionsMenu(menu)
//    }

    private fun setLogo(bitmap: Bitmap?) {
        binding.imgLogo.setImageBitmap(bitmap)

        binding.imgLogo.setOnTouchListener(MultiTouchListener())
    }

    private fun setImage(bitmap: Bitmap?) {

        binding.imgMain.setImageBitmap(bitmap)

        binding.imgMain.setOnTouchListener(MultiTouchListener())

        /*
        imageView = layoutInflater.inflate(
            R.layout.frame_image_view,
            binding.fotoBox,
            false
        )

        image = imageView?.findViewById(R.id.image_bubble)
        image?.setImageBitmap(bitmap)
        binding.fotoBox.addView(imageView)
        removeImage = imageView?.findViewById(R.id.imgImageClose)

        val frame: FrameLayout? = imageView?.findViewById(R.id.image_layout)

        imageView!!.setOnTouchListener(MultiTouchListener())

         */

    }

    private fun checkItemAvailability() {
        if (binding.imgMain.drawable != null) {
            binding.deleteItem.visibility = View.VISIBLE
        } else binding.deleteItem.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                GET_FROM_CAMERA -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    binding.imgMain.visibility = View.VISIBLE
                    binding.imgMain.setImageBitmap(selectedImage)
                }
                GET_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageUri = data.data
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                        setImage(bitmap)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                GET_LOGO_FROM_CAMERA -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    binding.imgLogo.visibility = View.VISIBLE
                    binding.imgLogo.setImageBitmap(selectedImage)
                }
                GET_LOGO_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageUri = data.data
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                        setLogo(bitmap)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun viewToImage(view: View): Bitmap? {

        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_save) {
            binding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(binding.fotoBox)
            if (post != null) {
                saveToInternalStorage(post)
            }
        }

        if (item.itemId == R.id.action_share) {
            binding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(binding.fotoBox)

            val uri = Uri.parse(
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    post,
                    null,
                    null
                )
            )

            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, uri)

            startActivity(Intent.createChooser(share, "Share Image"))

        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        val cw = ContextWrapper(applicationContext)
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, "UniqueFileName" + ".jpg")
        if (!file.exists()) {
            Log.d("path", file.toString())
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                Toast.makeText(this, "Post Saved", Toast.LENGTH_SHORT).show()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val GET_FROM_CAMERA = 3
        const val GET_FROM_GALLERY = 4

        const val GET_LOGO_FROM_CAMERA = 5
        const val GET_LOGO_FROM_GALLERY = 6
    }
}