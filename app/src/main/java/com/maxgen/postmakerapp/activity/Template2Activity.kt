package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.adapter.*
import com.maxgen.postmakerapp.databinding.ActivityTemplate2Binding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.viewmodel.TemplateViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.activity_template1.*
import java.io.FileNotFoundException
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
                        viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)
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
                        viewBinding.tvLogo.text = resources.getString(R.string.edit_logo)

                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun editText() {
        viewBinding.llMain.visibility = View.GONE
        viewBinding.llTextEdit.visibility = View.VISIBLE
        viewBinding.imgResize.visibility = View.GONE
        viewBinding.textResize.visibility = View.VISIBLE
    }

    override fun changeTextColor() {
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}

