package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.adapter.FontAdapter
import com.maxgen.postmakerapp.adapter.OnFontChangeListener
import com.maxgen.postmakerapp.databinding.ActivityCreatePostBinding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.multiTouchLib.MultiTouchListener
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CreatePostActivity : AppCompatActivity(), OnFontChangeListener {

    private lateinit var viewBinding: ActivityCreatePostBinding

    private var image: ImageView? = null
    private var textView: View? = null
    private var imageView: View? = null
    private var removeText: ImageView? = null
    private var removeImage: ImageView? = null
    private var editText: EditText? = null
    private var list: ArrayList<AssetModel>? = null
    private var fontAdapter: FontAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewBinding.edtWeb.isFocusableInTouchMode = false
        viewBinding.edtMain.isFocusableInTouchMode = false

        viewBinding.addImage.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->

                when {
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

        viewBinding.imgEditWeb.setOnClickListener {
            viewBinding.llMain.visibility = View.GONE
            viewBinding.llTextEdit.visibility = View.VISIBLE

            viewBinding.imgWebClose.visibility = View.VISIBLE
            viewBinding.imgEditWeb.visibility = View.GONE

            viewBinding.imgTextClose.visibility = View.GONE
            viewBinding.imgEdit.visibility = View.VISIBLE

            viewBinding.llDefaultFont.visibility = View.GONE
            viewBinding.llDone.visibility = View.GONE
            viewBinding.rv.visibility = View.GONE

            viewBinding.edtWeb.isFocusableInTouchMode = true
            viewBinding.edtMain.isFocusable = false
        }

        viewBinding.llDefaultFont.setOnClickListener {
            if (viewBinding.imgTextClose.visibility == View.VISIBLE) {
                viewBinding.edtMain.typeface = Typeface.DEFAULT
            }
            if (viewBinding.imgWebClose.visibility == View.VISIBLE) {
                viewBinding.edtWeb.typeface = Typeface.DEFAULT
            }
        }

        viewBinding.llDone.setOnClickListener {
            viewBinding.rv.visibility = View.GONE
            viewBinding.llDone.visibility = View.GONE

            viewBinding.llDefaultFont.visibility = View.GONE

            viewBinding.llTextEdit.visibility = View.GONE
            viewBinding.llMain.visibility = View.VISIBLE

            viewBinding.imgTextClose.visibility = View.GONE
            viewBinding.imgEdit.visibility = View.VISIBLE

            viewBinding.imgWebClose.visibility = View.GONE
            viewBinding.imgEditWeb.visibility = View.VISIBLE
        }
        viewBinding.imgFont.setOnClickListener {
            viewBinding.llMain.visibility = View.GONE
            viewBinding.llTextEdit.visibility = View.GONE
            viewBinding.llDone.visibility = View.VISIBLE
            viewBinding.rv.visibility = View.VISIBLE
            viewBinding.llDefaultFont.visibility = View.VISIBLE
            list = ArrayList()
            val assetManager: AssetManager = this.resources.assets

            val files = assetManager.list("fonts")

            if (files != null) {
                for (file in files) {
                    list!!.add(AssetModel("fonts/$file"))
                }
                fontAdapter = FontAdapter(list!!, this, this)
                viewBinding.rv.adapter = fontAdapter
                viewBinding.rv.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        viewBinding.imgEdit.setOnClickListener {
            viewBinding.llMain.visibility = View.GONE
            viewBinding.llTextEdit.visibility = View.VISIBLE

            viewBinding.imgTextClose.visibility = View.VISIBLE
            viewBinding.imgEdit.visibility = View.GONE

            viewBinding.imgEditWeb.visibility = View.VISIBLE
            viewBinding.imgWebClose.visibility = View.GONE

            viewBinding.llDefaultFont.visibility = View.GONE
            viewBinding.llDone.visibility = View.GONE
            viewBinding.rv.visibility = View.GONE
            // binding.llFont.visibility=View.GONE

            viewBinding.edtMain.isFocusableInTouchMode = true
            viewBinding.edtWeb.isFocusable = false
        }

        viewBinding.imgDone.setOnClickListener {
            viewBinding.llMain.visibility = View.VISIBLE
            viewBinding.llTextEdit.visibility = View.GONE
            viewBinding.imgTextClose.visibility = View.GONE
            viewBinding.imgEdit.visibility = View.VISIBLE
            viewBinding.imgWebClose.visibility = View.GONE
            viewBinding.imgEditWeb.visibility = View.VISIBLE
            viewBinding.edtWeb.isFocusable = false
            viewBinding.edtMain.isFocusable = false
        }

        viewBinding.imgTextClose.setOnClickListener {
            viewBinding.edtMain.setText("")
        }

        viewBinding.imgTxtColor.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("SELECT", object : ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        if (viewBinding.edtMain.isFocused)
                            viewBinding.edtMain.setTextColor(envelope!!.color)
                        if (viewBinding.edtWeb.isFocused)
                            viewBinding.edtWeb.setTextColor(envelope!!.color)
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

        viewBinding.addWebsite.setOnClickListener {
            viewBinding.webLayout.visibility = View.VISIBLE
            viewBinding.webLayout.setOnTouchListener(MultiTouchListener())

        }

        viewBinding.addText.setOnClickListener {
            viewBinding.edtLayout.visibility = View.VISIBLE
            viewBinding.edtLayout.setOnTouchListener(MultiTouchListener())

        }

        viewBinding.addLogo.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose your profile picture")
            builder.setItems(options) { dialog, item ->

                when {
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

        viewBinding.deleteItem.setOnClickListener {
            val alertBuilder = AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("All items in your post will be removed")
                .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                    viewBinding.imgMain.setImageBitmap(null)
                    viewBinding.imgLogo.removeSticker()
                    viewBinding.edtLayout.visibility = View.GONE
                    viewBinding.webLayout.visibility = View.GONE

                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })
            alertBuilder.show()
        }
        viewBinding.imgTxtStyle.setOnClickListener {
            when {
                viewBinding.edtMain.typeface == Typeface.DEFAULT -> {
                    viewBinding.edtMain.setTypeface(viewBinding.edtMain.typeface, Typeface.BOLD)
                }
                viewBinding.edtMain.typeface.isBold -> {
                    viewBinding.edtMain.setTypeface(viewBinding.edtMain.typeface, Typeface.ITALIC)
                }
                viewBinding.edtMain.typeface.isItalic -> {
                    viewBinding.edtMain.typeface == Typeface.DEFAULT
                }
            }
        }

    }

    private fun setLogo(bitmap: Bitmap?) {
        viewBinding.imgLogo.addSticker(bitmap)
    }

    private fun setImage(bitmap: Bitmap?) {

        viewBinding.imgMain.setImageBitmap(bitmap)

        viewBinding.imgMain.setOnTouchListener(MultiTouchListener())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {

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
            viewBinding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(viewBinding.fotoBox)
            if (post != null) {
                saveToExternalStorage(post)
            }
        }

        if (item.itemId == R.id.action_share) {
            if (viewBinding.edtMain.text.isNullOrEmpty()) {
                viewBinding.edtMain.visibility = View.GONE
            }

            if (viewBinding.edtWeb.text.isNullOrEmpty()) {
                viewBinding.edtWeb.visibility = View.GONE
            }
            viewBinding.imgEditWeb.visibility = View.GONE
            viewBinding.imgEdit.visibility = View.GONE
            viewBinding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(viewBinding.fotoBox)

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

    private fun saveToExternalStorage(image_bitmap: Bitmap) {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

        val myDir = File(root, "/saved_images")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        val fname = "Image-" + "image_name" + ".jpg"
        val file = File(myDir, fname)
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile() // if file already exists will do nothing
            val out = FileOutputStream(file)
            image_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        MediaScannerConnection.scanFile(
            this,
            arrayOf<String>(file.toString()),
            arrayOf<String>(file.getName()),
            null
        )
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        val path = File(ContextWrapper(this).filesDir, "MyAppName" + File.separator + "Images")
        if (!path.exists()) {
            path.mkdirs()
        }
        val outFile = File(path, "imageName" + ".jpeg")
        val outputStream = FileOutputStream(outFile)
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
    }

    companion object {
        const val GET_FROM_APP = 1
        const val GET_LOGO_FROM_APP = 2
        const val GET_FROM_GALLERY = 3
        const val GET_LOGO_FROM_GALLERY = 4
    }

    override fun onFontChange(typeface: Typeface) {
        if (viewBinding.imgTextClose.visibility == View.VISIBLE) {
            viewBinding.edtMain.typeface = typeface
        }
        if (viewBinding.imgWebClose.visibility == View.VISIBLE) {
            viewBinding.edtWeb.typeface = typeface
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}