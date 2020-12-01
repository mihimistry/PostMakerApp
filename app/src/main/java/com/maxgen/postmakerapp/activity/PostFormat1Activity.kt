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
import com.maxgen.postmakerapp.databinding.ActivityPostFormat1Binding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.multiTouchLib.MultiTouchListener
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PostFormat1Activity : AppCompatActivity(), OnFontChangeListener {

    private lateinit var binding: ActivityPostFormat1Binding

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
        binding = ActivityPostFormat1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.edtWeb.isFocusableInTouchMode = false
        binding.edtMain.isFocusableInTouchMode = false


        binding.addImage.setOnClickListener {
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

        binding.imgEditWeb.setOnClickListener {
            binding.llMain.visibility = View.GONE
            binding.llTextEdit.visibility = View.VISIBLE

            binding.imgWebClose.visibility = View.VISIBLE
            binding.imgEditWeb.visibility = View.GONE

            binding.imgTextClose.visibility = View.GONE
            binding.imgEdit.visibility = View.VISIBLE

            binding.llDefaultFont.visibility = View.GONE
            binding.llDone.visibility = View.GONE
            binding.rv.visibility = View.GONE

            binding.edtWeb.isFocusableInTouchMode = true
            binding.edtMain.isFocusable = false
        }

        binding.llDefaultFont.setOnClickListener {
            if (binding.imgTextClose.visibility == View.VISIBLE) {
                binding.edtMain.typeface = Typeface.DEFAULT
            }
            if (binding.imgWebClose.visibility == View.VISIBLE) {
                binding.edtWeb.typeface = Typeface.DEFAULT
            }
        }

        binding.llDone.setOnClickListener {
            binding.rv.visibility = View.GONE
            binding.llDone.visibility = View.GONE

            binding.llDefaultFont.visibility = View.GONE

            binding.llTextEdit.visibility = View.GONE
            binding.llMain.visibility = View.VISIBLE

            binding.imgTextClose.visibility = View.GONE
            binding.imgEdit.visibility = View.VISIBLE

            binding.imgWebClose.visibility = View.GONE
            binding.imgEditWeb.visibility = View.VISIBLE
        }
        binding.imgFont.setOnClickListener {
            binding.llMain.visibility = View.GONE
            binding.llTextEdit.visibility = View.GONE
            binding.llDone.visibility = View.VISIBLE
            binding.rv.visibility = View.VISIBLE
            binding.llDefaultFont.visibility = View.VISIBLE
            list = ArrayList()
            val assetManager: AssetManager = this.resources.assets

            val files = assetManager.list("fonts")

            if (files != null) {
                for (file in files) {
                    list!!.add(AssetModel("fonts/$file"))
                }
                fontAdapter = FontAdapter(list!!, this, this)
                binding.rv.adapter = fontAdapter
                binding.rv.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        binding.imgEdit.setOnClickListener {
            binding.llMain.visibility = View.GONE
            binding.llTextEdit.visibility = View.VISIBLE

            binding.imgTextClose.visibility = View.VISIBLE
            binding.imgEdit.visibility = View.GONE

            binding.imgEditWeb.visibility = View.VISIBLE
            binding.imgWebClose.visibility = View.GONE

            binding.llDefaultFont.visibility = View.GONE
            binding.llDone.visibility = View.GONE
            binding.rv.visibility = View.GONE
            // binding.llFont.visibility=View.GONE

            binding.edtMain.isFocusableInTouchMode = true
            binding.edtWeb.isFocusable = false
        }

        binding.imgDone.setOnClickListener {
            binding.llMain.visibility = View.VISIBLE
            binding.llTextEdit.visibility = View.GONE
            binding.imgTextClose.visibility = View.GONE
            binding.imgEdit.visibility = View.VISIBLE
            binding.imgWebClose.visibility = View.GONE
            binding.imgEditWeb.visibility = View.VISIBLE
            binding.edtWeb.isFocusable = false
            binding.edtMain.isFocusable = false
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

        binding.addWebsite.setOnClickListener {
            binding.webLayout.visibility = View.VISIBLE
            binding.webLayout.setOnTouchListener(MultiTouchListener())
        }

        binding.addText.setOnClickListener {

            binding.edtLayout.visibility = View.VISIBLE

            binding.edtLayout.setOnTouchListener(MultiTouchListener())

        }

        binding.addLogo.setOnClickListener {
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

        binding.deleteItem.setOnClickListener {
            val alertBuilder = AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("All items in your post will be removed")
                .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->
                    binding.imgMain.setImageBitmap(null)
                    binding.imgLogo.removeSticker()
                    binding.edtLayout.visibility = View.GONE
                    binding.webLayout.visibility = View.GONE

                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })
            alertBuilder.show()
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

    }

    private fun setLogo(bitmap: Bitmap?) {
        binding.imgLogo.addSticker(bitmap)
    }

    private fun setImage(bitmap: Bitmap?) {

        binding.imgMain.setImageBitmap(bitmap)

        binding.imgMain.setOnTouchListener(MultiTouchListener())

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
            binding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(binding.fotoBox)
            if (post != null) {
                saveToExternalStorage(post)
            }
        }

        if (item.itemId == R.id.action_share) {
            if (binding.edtMain.text.isNullOrEmpty()) {
                binding.edtMain.visibility = View.GONE
            }

            if (binding.edtWeb.text.isNullOrEmpty()) {
                binding.edtWeb.visibility = View.GONE
            }
            binding.imgEditWeb.visibility = View.GONE
            binding.imgEdit.visibility = View.GONE
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
        const val GET_FROM_GALLERY = 3
        const val GET_LOGO_FROM_GALLERY = 4
    }

    override fun onFontChange(typeface: Typeface) {
        if (binding.imgTextClose.visibility == View.VISIBLE) {
            binding.edtMain.typeface = typeface
        }
        if (binding.imgWebClose.visibility == View.VISIBLE) {
            binding.edtWeb.typeface = typeface
        }
    }
}