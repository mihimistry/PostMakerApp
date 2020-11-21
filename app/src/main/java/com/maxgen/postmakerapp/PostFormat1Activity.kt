package com.maxgen.postmakerapp

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.maxgen.postmakerapp.databinding.ActivityPostFormat1Binding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class PostFormat1Activity : AppCompatActivity(), View.OnTouchListener, View.OnClickListener {

    private lateinit var binding: ActivityPostFormat1Binding
    var matrix: Matrix = Matrix()
    var savedMatrix: Matrix = Matrix()

    private var imageview: ImageView? = null
    var dX: Float = 0f
    var dY: kotlin.Float = 0f

    // We can be in one of these 3 states
    val NONE = 0
    val DRAG = 1
    val ZOOM = 2
    var mode = NONE

    // Remember some things for zooming
    var start = PointF()
    var mid = PointF()
    var oldDist = 1f
    var scaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostFormat1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //   binding.imgMain.setOnTouchListener(this)

        //   binding.imgMain.performClick()

        binding.addImage.setOnClickListener(this)

      //  checkItemAvailability()
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener(mScaleFactor,imageview))

        binding.addText.setOnClickListener {
            val editText=EditText(this)
            val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )

            // Add image path from drawable folder.

            // Add image path from drawable folder.

            editText.hint="ENTER TEXT HERE"
            editText.layoutParams = params
            binding.fotoBox.addView(editText)
            editText.setOnTouchListener(this)
            editText.setBackgroundColor(resources.getColor(android.R.color.transparent))
            editText.setHintTextColor(Color.parseColor("#000000"))
            editText.setTextColor(Color.parseColor("#000000"))
            editText.isFocusable = true
            editText.requestFocus()

        }

        binding.deleteItem.setOnClickListener {
            binding.fotoBox.removeAllViewsInLayout()

        }

        binding.edtMain.setOnTouchListener(this)
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
    }

    private fun checkItemAvailability() {
        if (binding.imgMain.drawable != null) {
            binding.deleteItem.visibility = View.VISIBLE
        } else binding.deleteItem.visibility = View.GONE
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        scaleGestureDetector?.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                v.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
            }

            else -> return false
        }
        return true
    }

    override fun onClick(v: View?) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options) { dialog, item ->

            when {
                options[item] == "Take Photo" -> {

                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, GET_FROM_CAMARA)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                GET_FROM_CAMARA -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    binding.imgMain.visibility = View.VISIBLE
                    binding.imgMain.setImageBitmap(selectedImage)
                }
                GET_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageUri = data.data
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        imageview = ImageView(this)
                        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )

                        // Add image path from drawable folder.

                        // Add image path from drawable folder.
                        imageview?.setImageBitmap(bitmap)
                        imageview?.layoutParams = params
                        imageview?.scaleType = ImageView.ScaleType.MATRIX
                        binding.fotoBox.addView(imageview)
                        imageview?.setOnTouchListener(this)

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
            val post: Bitmap? = viewToImage(binding.cvPost)
            if (post != null) {
                saveToInternalStorage(post)
            }
        }
        if (item.itemId == R.id.action_share) {
            binding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(binding.cvPost)

            val uri = Uri.parse(
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    post,
                    null,
                    null
                )
            )

            // use intent to share image
            // use intent to share image
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

    private class ScaleListener(
        var mScaleFactor: Float,
        var imageview: ImageView?
    ) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = 0.1f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f))
            imageview?.scaleX = mScaleFactor
            imageview?.scaleY = mScaleFactor
            return true
        }
    }
    companion object {
        const val GET_FROM_CAMARA = 3
        const val GET_FROM_GALLERY = 4
    }
}