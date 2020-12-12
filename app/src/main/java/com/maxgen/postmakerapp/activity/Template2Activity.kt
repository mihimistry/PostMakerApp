package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.AssetManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.adapter.*
import com.maxgen.postmakerapp.databinding.ActivityTemplate2Binding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.TemplateViewModel
import kotlinx.android.synthetic.main.activity_template1.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class Template2Activity : AppCompatActivity(), OnAddImagesListener, OnTemplateClickListeners,
    OnCornerSelectionListener, OnFontChangeListener {
    private lateinit var viewBinding: ActivityTemplate2Binding
    private var list: ArrayList<AssetModel>? = null
    private var fontAdapter: FontAdapter? = null
    private var logoBitmap: Bitmap? = null
    private var viewModel: TemplateViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTemplate2Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adRequest = AdRequest.Builder().build()
        viewBinding.adView.loadAd(adRequest)

        setViewModel()
        setUI()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(TemplateViewModel::class.java)
        viewBinding.viewModel = viewModel
        viewModel?.imageListener = this
        viewModel?.clickListeners = this
        viewModel?.cornerSelectionListener = this
    }

    private fun setUI() {
        if (viewBinding.logo.drawable == null)
            viewBinding.tvLogo.text = resources.getString(R.string.add_logo)
        else viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)

        val user = SharedPreferenceUser.getInstance().getUser(this)
        viewBinding.user = user
        Glide.with(this).load(user.imageUrl).into(viewBinding.logo)

        if (user.email.isNotEmpty() && user.phone.isNotEmpty()) {
            viewBinding.edtWeb.setText(user.website + " | " + user.phone)
        } else {
            viewBinding.edtWeb.setText(user.website + user.phone)
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

                        viewBinding.logo.layoutParams.width = 5 * progress
                        viewBinding.logo.layoutParams.height = 5 * progress
                        viewBinding.logo.requestLayout()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                CreatePostActivity.GET_FROM_APP -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val byteArray = data.getByteArrayExtra("image")
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                        Glide.with(this).load(bitmap).into(viewBinding.imgMain)

                    }
                }
                CreatePostActivity.GET_LOGO_FROM_APP -> {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        val byteArray = data.getByteArrayExtra("image")
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                        Glide.with(this).load(bitmap).into(viewBinding.logo)
                        viewBinding.llSelector.visibility = View.VISIBLE
                        viewBinding.logo.visibility = View.VISIBLE
                        viewBinding.constraint.visibility = View.GONE
                        if (bitmap != null) viewBinding.tvLogo.text =
                            resources.getString(R.string.edit_logo)
                    }
                }
                CreatePostActivity.GET_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    var bitmap: Bitmap? = null

                    try {
                        bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        viewBinding.imgMain.setImageBitmap(bitmap)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                CreatePostActivity.GET_LOGO_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    try {

                        logoBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        viewBinding.llSelector.visibility = View.VISIBLE
                        viewBinding.logo.visibility = View.VISIBLE
                        viewBinding.constraint.visibility = View.GONE
                        viewBinding.logo.setImageBitmap(logoBitmap)
                        if (logoBitmap != null) viewBinding.tvLogo.text =
                            resources.getString(R.string.edit_logo)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun menuItemSelected(it: MenuItem?) {
        if (it?.itemId == R.id.action_save) {

        }

        if (it?.itemId == R.id.action_share) {
            if (viewBinding.edtMain.text.isNullOrEmpty()) {
                viewBinding.edtMain.visibility = View.GONE
            }

            if (viewBinding.edtWeb.text.isNullOrEmpty()) {
                viewBinding.edtWeb.visibility = View.GONE
            }
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
            viewBinding.edtMain.visibility = View.VISIBLE
            viewBinding.edtWeb.visibility = View.VISIBLE
            viewBinding.edtMain.isCursorVisible = true

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
            }
            R.id.action_share -> {
                var uri: Uri? = null
                if (viewBinding.edtMain.text.isNullOrEmpty()) {
                    viewBinding.edtMain.visibility = View.GONE
                }

                if (viewBinding.edtWeb.text.isNullOrEmpty()) {
                    viewBinding.edtWeb.visibility = View.GONE
                }
                viewBinding.edtMain.isCursorVisible = false
                val post: Bitmap? = viewToImage(viewBinding.fotoBox)

                saveImageToInternalStorage(post)


            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveImageToInternalStorage(post: Bitmap?) {
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/image.png") // overwrites this image every time
            post?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            shareImage()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun shareImage() {
        val imagePath: File = File(cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri: Uri =
            FileProvider.getUriForFile(this, "com.maxgen.postmakerapp.fileprovider", newFile)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND

        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(shareIntent, "Choose an app"))

        viewBinding.edtMain.visibility = View.VISIBLE
        viewBinding.edtWeb.visibility = View.VISIBLE
        viewBinding.edtMain.isCursorVisible = true
    }

    private fun checkLogoDrawable() {
        if (viewBinding.logo.drawable == null)
            viewBinding.tvLogo.text = resources.getString(R.string.add_logo)
        else viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)
    }

    override fun editText() {
        viewBinding.llMain.visibility = View.GONE
        viewBinding.llTextEdit.visibility = View.VISIBLE
        viewBinding.seekBarLayout.visibility = View.GONE
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
    }

    override fun doneChangeFont() {
        viewBinding.rv.visibility = View.GONE
        viewBinding.llDone.visibility = View.GONE
        viewBinding.llDefaultFont.visibility = View.GONE
        viewBinding.llTextEdit.visibility = View.VISIBLE
    }

    override fun setDefaultFont() {
        if (viewBinding.edtMain.isFocused) {
            viewBinding.edtMain.typeface = Typeface.DEFAULT
        }
        if (viewBinding.edtWeb.isFocused) {
            viewBinding.edtWeb.typeface = Typeface.DEFAULT
        }
    }

    override fun getPreviousActivity() {
        finish()
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
        viewBinding.logo.setImageDrawable(null)
        viewBinding.imgMain.setImageDrawable(null)
        checkLogoDrawable()

    }

    override fun closeWebEdit() {
        TODO("Not yet implemented")
    }

    override fun closeQuoteEdit() {
        TODO("Not yet implemented")
    }

    override fun editWebText() {
        TODO("Not yet implemented")
    }

    override fun onFontChange(typeface: Typeface) {
        if (viewBinding.edtMain.isFocused) {
            viewBinding.edtMain.typeface = typeface
        }
        if (viewBinding.edtWeb.isFocused) {
            viewBinding.edtWeb.typeface = typeface
        }
    }

    override fun changeLogoPosition() {
        viewBinding.llSelector.visibility = View.VISIBLE
        viewBinding.constraint.visibility = View.GONE
    }

    override fun setLogoOnTSCorner() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(viewBinding.constraint)

        constraintSet.clear(R.id.logo, ConstraintSet.BOTTOM)
        constraintSet.clear(R.id.logo, ConstraintSet.END)
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.applyTo(viewBinding.constraint)
        viewBinding.llSelector.visibility = View.GONE
        viewBinding.constraint.visibility = View.VISIBLE

    }

    override fun setLogoOnTECorner() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(viewBinding.constraint)

        constraintSet.clear(R.id.logo, ConstraintSet.BOTTOM)
        constraintSet.clear(R.id.logo, ConstraintSet.START)
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.applyTo(viewBinding.constraint)
        viewBinding.llSelector.visibility = View.GONE
        viewBinding.constraint.visibility = View.VISIBLE

    }

    override fun setLogoOnBSCorner() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(viewBinding.constraint)

        constraintSet.clear(R.id.logo, ConstraintSet.TOP)
        constraintSet.clear(R.id.logo, ConstraintSet.END)
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.applyTo(viewBinding.constraint)
        viewBinding.llSelector.visibility = View.GONE
        viewBinding.constraint.visibility = View.VISIBLE

    }

    override fun setLogoOnBECorner() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(viewBinding.constraint)

        constraintSet.clear(R.id.logo, ConstraintSet.TOP)
        constraintSet.clear(R.id.logo, ConstraintSet.START)
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.connect(
            R.id.logo,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            resources.getDimensionPixelSize(R.dimen._10sdp)
        )
        constraintSet.applyTo(viewBinding.constraint)
        viewBinding.llSelector.visibility = View.GONE
        viewBinding.constraint.visibility = View.VISIBLE

    }

    override fun removeLogo() {
        viewBinding.logo.setImageBitmap(null)
    }

    override fun resizeLogo() {
        viewBinding.seekBarLayout.visibility = View.VISIBLE
        viewBinding.imgResize.visibility = View.VISIBLE
        viewBinding.textResize.visibility = View.GONE
        viewBinding.textShadow.visibility = View.GONE
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}

