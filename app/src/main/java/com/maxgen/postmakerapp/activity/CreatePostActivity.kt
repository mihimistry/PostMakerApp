package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.*
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
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.adapter.FontAdapter
import com.maxgen.postmakerapp.adapter.OnAddImagesListener
import com.maxgen.postmakerapp.adapter.OnFontChangeListener
import com.maxgen.postmakerapp.adapter.OnTemplateClickListeners
import com.maxgen.postmakerapp.databinding.ActivityCreatePostBinding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.multiTouchLib.MultiTouchListener
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.TemplateViewModel
import kotlinx.android.synthetic.main.activity_template1.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class CreatePostActivity : AppCompatActivity(), OnFontChangeListener, OnTemplateClickListeners,
    OnAddImagesListener {

    private lateinit var viewBinding: ActivityCreatePostBinding

    private var image: ImageView? = null
    private var textView: View? = null
    private var imageView: View? = null
    private var removeText: ImageView? = null
    private var removeImage: ImageView? = null
    private var editText: EditText? = null
    private var list: ArrayList<AssetModel>? = null
    private var fontAdapter: FontAdapter? = null
    private var viewModel: TemplateViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val adRequest = AdRequest.Builder().build()
        viewBinding.adView.loadAd(adRequest)

        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewBinding.edtWeb.isFocusableInTouchMode = false
        viewBinding.edtMain.isFocusableInTouchMode = false

        viewModel = ViewModelProvider(this).get(TemplateViewModel::class.java)
        viewBinding.viewModel = viewModel

        viewModel?.clickListeners = this
        viewModel?.imageListener = this

        setupUI()


    }

    private fun setupUI() {

        viewBinding.webLayout.setOnTouchListener(MultiTouchListener())
        viewBinding.edtLayout.setOnTouchListener(MultiTouchListener())

        if (viewBinding.imgLogo.drawable == null)
            viewBinding.tvLogo.text = resources.getString(R.string.add_logo)
        else viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)

        val user = SharedPreferenceUser.getInstance().getUser(this)
        viewBinding.user = user

        CoroutineScope(IO).launch {
            loadImage(user)
        }

        if (user.email.isNotEmpty() && user.phone.isNotEmpty()) {
            viewBinding.edtWeb.setText(user.website + " | " + user.phone)
        } else {
            viewBinding.edtWeb.setText(user.website + user.phone)
        }

        viewBinding.toolbar.setOnMenuItemClickListener {
            menuItemSelected(it)
            return@setOnMenuItemClickListener false
        }

        viewBinding.llLogo.setOnClickListener {
            if (viewBinding.tvLogo.text == resources.getString(R.string.add_logo))
                viewModel?.onAddLogoSelected(this)

            if (viewBinding.tvLogo.text == resources.getString(R.string.edit_logo))
                viewModel?.onEditLogoSelected(this)
        }

        viewBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress != 0) {
                    if (viewBinding.imgResize.visibility == View.VISIBLE) {

                        viewBinding.imgLogo.layoutParams.width = 5 * progress
                        viewBinding.imgLogo.layoutParams.height = 5 * progress
                        viewBinding.imgLogo.requestLayout()
                    }

                    if (viewBinding.textResize.visibility == View.VISIBLE) {

                        if (edt_main.isFocused) {
                            edt_main.textSize = progress.toFloat()
                            edt_main.requestLayout()
                        }
                        if (edt_web.isFocused) {
                            edt_web.textSize = progress.toFloat()
                            edt_web.requestLayout()
                        }
                    }
                    if (viewBinding.textShadow.visibility == View.VISIBLE) {

                        if (edt_main.isFocused) {
                            viewBinding.edtMain
                                .setShadowLayer(
                                    (progress / 10).toFloat(),
                                    (progress / 10).toFloat(),
                                    (progress / 10).toFloat(),
                                    Color.BLACK
                                );
                            edt_main.requestLayout()
                        }
                        if (edt_web.isFocused) {
                            viewBinding.edtWeb.setShadowLayer(
                                (progress / 10).toFloat(),
                                (progress / 10).toFloat(),
                                (progress / 10).toFloat(),
                                Color.BLACK
                            );
                            edt_web.requestLayout()
                        }
                    }
                    viewBinding.tvPer.text = "$progress%"
                }
            }

            override fun onStartTrackingTouch(seekbar: SeekBar) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {}
        })


        viewBinding.imgTextClose.setOnClickListener {
            viewBinding.edtMain.setText("")
        }


    }

    private fun loadImage(user: UserModel) {
        val url = URL(user.imageUrl)
        val image: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        viewBinding.imgLogo.addSticker(image)
    }

    private fun menuItemSelected(it: MenuItem?) {

        if (it?.itemId == R.id.action_save) {
            viewBinding.edtMain.isCursorVisible = false
            val post: Bitmap? = viewToImage(viewBinding.fotoBox)
            if (post != null) {
                saveToExternalStorage(post)
            }
        }

        if (it?.itemId == R.id.action_share) {
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
                    post!!,
                    null,
                    null
                )
            )

            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/*"
            share.putExtra(Intent.EXTRA_STREAM, uri)

            startActivity(Intent.createChooser(share, "Share Image"))

        }

    }

    private fun setLogo(bitmap: Bitmap?) = viewBinding.imgLogo.addSticker(bitmap)


    private fun setImage(bitmap: Bitmap?) {
        viewBinding.imgMain.setImageBitmap(bitmap)
        viewBinding.imgMain.setOnTouchListener(MultiTouchListener())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                GET_FROM_APP -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val byteArray = data.getByteArrayExtra("image")
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                        if (bitmap != null) setImage(bitmap)
                    }
                }
                GET_LOGO_FROM_APP -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val byteArray = data.getByteArrayExtra("image")
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                        if (bitmap != null) {
                            setLogo(bitmap)
                            viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)
                        }
                    }
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
        const val GET_FROM_CAMERA = 5
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

    override fun editText() {
        viewBinding.llMain.visibility = View.GONE
        viewBinding.llTextEdit.visibility = View.VISIBLE
        viewBinding.seekBarLayout.visibility = View.GONE

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

    override fun changeTextColor(lastSelectedColor: Int) {

        if (viewBinding.edtMain.isFocused)
            viewBinding.edtMain.setTextColor(lastSelectedColor)
        if (viewBinding.edtWeb.isFocused)
            viewBinding.edtWeb.setTextColor(lastSelectedColor)

    }

    override fun changeTextStyle() {
        if (viewBinding.edtMain.isFocused) {
            when {
                viewBinding.edtMain.typeface == Typeface.DEFAULT -> {
                    viewBinding.edtMain.setTypeface(viewBinding.edtMain.typeface, Typeface.BOLD)
                }
                viewBinding.edtMain.typeface.isBold -> {
                    viewBinding.edtMain.setTypeface(viewBinding.edtMain.typeface, Typeface.ITALIC)
                }
                viewBinding.edtMain.typeface.isItalic -> {
                    viewBinding.edtMain.typeface = Typeface.DEFAULT
                }
            }
        }

        if (viewBinding.edtWeb.isFocused) {
            when {
                viewBinding.edtWeb.typeface == Typeface.DEFAULT -> {
                    viewBinding.edtWeb.setTypeface(viewBinding.edtMain.typeface, Typeface.BOLD)
                }
                viewBinding.edtWeb.typeface.isBold -> {
                    viewBinding.edtWeb.setTypeface(viewBinding.edtMain.typeface, Typeface.ITALIC)
                }
                viewBinding.edtWeb.typeface.isItalic -> {
                    viewBinding.edtWeb.typeface = Typeface.DEFAULT
                }
            }
        }
    }

    override fun changeTextFont() {
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


    override fun doneTextEditing() {
        viewBinding.seekBarLayout.visibility = View.GONE
        viewBinding.llMain.visibility = View.VISIBLE
        viewBinding.llTextEdit.visibility = View.GONE
        viewBinding.textResize.visibility = View.GONE
        viewBinding.imgResize.visibility = View.VISIBLE
        viewBinding.imgTextClose.visibility = View.GONE
        viewBinding.imgEdit.visibility = View.VISIBLE
        viewBinding.imgWebClose.visibility = View.GONE
        viewBinding.imgEditWeb.visibility = View.VISIBLE
        viewBinding.edtWeb.isFocusable = false
        viewBinding.edtMain.isFocusable = false
    }

    override fun doneChangeFont() {
        viewBinding.rv.visibility = View.GONE
        viewBinding.llDone.visibility = View.GONE
        viewBinding.llDefaultFont.visibility = View.GONE
        viewBinding.llTextEdit.visibility = View.VISIBLE

        viewBinding.llTextEdit.visibility = View.GONE
        viewBinding.llMain.visibility = View.VISIBLE

        viewBinding.imgTextClose.visibility = View.GONE
        viewBinding.imgEdit.visibility = View.VISIBLE

        viewBinding.imgWebClose.visibility = View.GONE
        viewBinding.imgEditWeb.visibility = View.VISIBLE
    }

    override fun setDefaultFont() {
        if (viewBinding.edtMain.isFocused) {
            viewBinding.edtMain.typeface = Typeface.DEFAULT
        }
        if (viewBinding.edtWeb.isFocused) {
            viewBinding.edtWeb.typeface = Typeface.DEFAULT
        }
        if (viewBinding.imgTextClose.visibility == View.VISIBLE) {
            viewBinding.edtMain.typeface = Typeface.DEFAULT
        }
        if (viewBinding.imgWebClose.visibility == View.VISIBLE) {
            viewBinding.edtWeb.typeface = Typeface.DEFAULT
        }
    }

    override fun getPreviousActivity() {
        TODO("Not yet implemented")
    }

    override fun addTextShadow() {
        viewBinding.seekBarLayout.visibility = View.VISIBLE
        viewBinding.imgResize.visibility = View.GONE
        viewBinding.textResize.visibility = View.GONE
        viewBinding.textShadow.visibility = View.VISIBLE
    }

    override fun onSeekDone() {
        viewBinding.seekBarLayout.visibility = View.GONE
    }

    override fun changeTextSize() {
        viewBinding.seekBarLayout.visibility = View.VISIBLE
        viewBinding.imgResize.visibility = View.GONE
        viewBinding.textResize.visibility = View.VISIBLE
        viewBinding.textShadow.visibility = View.GONE
    }

    override fun deleteAllItems() {
        viewBinding.edtMain.setText("")
        viewBinding.edtWeb.setText("")
        viewBinding.imgLogo.setImageDrawable(null)
        viewBinding.imgMain.setImageDrawable(null)
        viewBinding.imgMain.setImageBitmap(null)
        viewBinding.imgLogo.removeSticker()
        viewBinding.edtLayout.visibility = View.GONE
        viewBinding.webLayout.visibility = View.GONE
        checkLogoDrawable()

    }

    private fun checkLogoDrawable() {
        if (viewBinding.imgLogo.drawable == null)
            viewBinding.tvLogo.text = resources.getString(R.string.add_logo)
        else viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)
    }

    override fun closeWebEdit() {
        viewBinding.edtWeb.setText("")
    }

    override fun closeQuoteEdit() {
        viewBinding.edtMain.setText("")
    }

    override fun editWebText() {
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

    override fun getBackgroundImage(s: String) {
        if (s == resources.getString(R.string.from_gallery)) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                CreatePostActivity.GET_FROM_GALLERY
            )
        }
        if (s == resources.getString(R.string.from_app)) {
            startActivityForResult(
                Intent(this, ImageListActivity::class.java),
                CreatePostActivity.GET_FROM_APP
            )
        }
    }

    override fun getLogo(from: String) {
        if (from == resources.getString(R.string.from_gallery)) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                CreatePostActivity.GET_LOGO_FROM_GALLERY
            )
        }
        if (from == resources.getString(R.string.from_app)) {
            startActivityForResult(
                Intent(this, ImageListActivity::class.java),
                CreatePostActivity.GET_LOGO_FROM_APP
            )
        }
    }
}