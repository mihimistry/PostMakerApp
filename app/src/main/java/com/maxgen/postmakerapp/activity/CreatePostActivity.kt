package com.maxgen.postmakerapp.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.LoadAdError
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.adapter.FontAdapter
import com.maxgen.postmakerapp.adapter.OnAddImagesListener
import com.maxgen.postmakerapp.adapter.OnFontChangeListener
import com.maxgen.postmakerapp.adapter.OnTemplateClickListeners
import com.maxgen.postmakerapp.databinding.ActivityCreatePostBinding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.model.UserModel
import com.maxgen.postmakerapp.multiTouchLib.MultiTouchListener
import com.maxgen.postmakerapp.utils.MyUtils
import com.maxgen.postmakerapp.utils.SharedPreferenceUser
import com.maxgen.postmakerapp.viewmodel.TemplateViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_template1.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class CreatePostActivity : AppCompatActivity(), OnFontChangeListener, OnTemplateClickListeners,
    OnAddImagesListener {

    private lateinit var viewBinding: ActivityCreatePostBinding
    private lateinit var mInterstitialAd: InterstitialAd
    private val PERMISSION_REQUEST_CODE = 200

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

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
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

    override fun onStart() {
        super.onStart()

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.interstitial_ad_unit_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun checkPermission(): Boolean {
        val result: Int =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {

        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {

                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {

                    } else {
                        Toast.makeText(
                            this,
                            "Please Grant Permission to Save Post",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }
        }
    }

    private fun setupUI() {

        viewBinding.webLayout.setOnTouchListener(MultiTouchListener())
        viewBinding.edtLayout.setOnTouchListener(MultiTouchListener())

        if (viewBinding.imgLogo.drawable == null)
            viewBinding.tvLogo.text = resources.getString(R.string.add_logo)
        else viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)

        val user = SharedPreferenceUser.getInstance().getUser(this)
        viewBinding.user = user

        Observable.fromRunnable<Any> {
            loadImage(user)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()


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

        if (MyUtils.hasActiveInternetConnection(this)) {
            if (user.imageUrl.isNotEmpty()) {
                val url = URL(user.imageUrl)
                val image: Bitmap =
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                viewBinding.imgLogo.addSticker(image)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val post: Bitmap? = viewToImage(viewBinding.fotoBox)

        when (item.itemId) {
            R.id.action_save -> {

                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                    mInterstitialAd.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            // Code to be executed when an ad finishes loading.
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            // Code to be executed when an ad request fails.
                        }

                        override fun onAdOpened() {
                            // Code to be executed when the ad is displayed.
                        }

                        override fun onAdClicked() {
                            // Code to be executed when the user clicks on an ad.
                        }

                        override fun onAdLeftApplication() {
                            // Code to be executed when the user has left the app.
                        }

                        override fun onAdClosed() {
                            // Code to be executed when the interstitial ad is closed.
                            if (checkPermission())
                                MyUtils.saveMediaToStorage(this@CreatePostActivity, post)
                            else requestPermission()

                            viewBinding.imgEdit.visibility = View.VISIBLE
                            viewBinding.imgEditWeb.visibility = View.VISIBLE
                            viewBinding.edtLayout.visibility = View.VISIBLE
                            viewBinding.webLayout.visibility = View.VISIBLE
                            viewBinding.edtMain.isCursorVisible = true
                        }
                    }

                } else {
                    if (checkPermission())
                        MyUtils.saveMediaToStorage(this, post)
                    else requestPermission()

                    Log.d("TAG", "The interstitial wasn't loaded yet.")

                    viewBinding.imgEdit.visibility = View.VISIBLE
                    viewBinding.imgEditWeb.visibility = View.VISIBLE
                    viewBinding.edtLayout.visibility = View.VISIBLE
                    viewBinding.webLayout.visibility = View.VISIBLE
                    viewBinding.edtMain.isCursorVisible = true
                }
            }

            R.id.action_share -> {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                    mInterstitialAd.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            // Code to be executed when an ad finishes loading.
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            // Code to be executed when an ad request fails.
                        }

                        override fun onAdOpened() {
                            // Code to be executed when the ad is displayed.
                        }

                        override fun onAdClicked() {
                            // Code to be executed when the user clicks on an ad.
                        }

                        override fun onAdLeftApplication() {
                            // Code to be executed when the user has left the app.
                        }

                        override fun onAdClosed() {

                            saveImageToInternalStorage(post)
                            // Code to be executed when the interstitial ad is closed.
                        }
                    }
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.")

                    saveImageToInternalStorage(post)
                }

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


        viewBinding.imgEdit.visibility = View.VISIBLE
        viewBinding.imgEditWeb.visibility = View.VISIBLE
        viewBinding.edtLayout.visibility = View.VISIBLE
        viewBinding.webLayout.visibility = View.VISIBLE
        viewBinding.edtMain.isCursorVisible = true
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
        if (viewBinding.edtMain.text.isNullOrEmpty()) {
            viewBinding.edtLayout.visibility = View.GONE
        }

        if (viewBinding.edtWeb.text.isNullOrEmpty()) {
            viewBinding.webLayout.visibility = View.GONE
        }
        viewBinding.edtMain.isCursorVisible = false
        viewBinding.imgEdit.visibility = View.GONE
        viewBinding.imgEditWeb.visibility = View.GONE

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

    companion object {
        const val GET_FROM_APP = 1
        const val GET_LOGO_FROM_APP = 2
        const val GET_FROM_GALLERY = 3
        const val GET_LOGO_FROM_GALLERY = 4
        const val GET_FROM_CAMERA = 5
        private const val TAG = "CreatePostActivity"
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
        // viewBinding.imgLogo.removeSticker()
        //  viewBinding.edtLayout.visibility = View.GONE
        //  viewBinding.webLayout.visibility = View.GONE
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