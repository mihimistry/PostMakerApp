package com.maxgen.postmakerapp.activity

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.maxgen.postmakerapp.activity.PostFormat1Activity.Companion.GET_FROM_GALLERY
import com.maxgen.postmakerapp.activity.PostFormat1Activity.Companion.GET_LOGO_FROM_GALLERY
import com.maxgen.postmakerapp.adapter.FontAdapter
import com.maxgen.postmakerapp.adapter.OnAddImagesListener
import com.maxgen.postmakerapp.adapter.OnFontChangeListener
import com.maxgen.postmakerapp.adapter.OnTemplateClickListeners
import com.maxgen.postmakerapp.databinding.ActivityTemplate1Binding
import com.maxgen.postmakerapp.model.AssetModel
import com.maxgen.postmakerapp.viewmodel.TemplateViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.activity_template1.*
import java.io.FileNotFoundException
import java.io.IOException


class Template1Activity : AppCompatActivity(), OnAddImagesListener, OnTemplateClickListeners, OnFontChangeListener {
    private lateinit var viewBinding: ActivityTemplate1Binding
    private var list: ArrayList<AssetModel>? = null
    private var fontAdapter: FontAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTemplate1Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val viewModel = ViewModelProvider(this).get(TemplateViewModel::class.java)
        viewBinding.viewModel = viewModel
        viewModel.imageListener = this
        viewModel.clickListeners = this
        val adRequest = AdRequest.Builder().build()
        viewBinding.adView.loadAd(adRequest)

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
                    viewBinding.tvPer.text = progress.toString()
                }
            }

            override fun onStartTrackingTouch(seekbar: SeekBar) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {}
        })

    }

    override fun getBackgroundImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GET_FROM_GALLERY)
    }

    override fun getLogo() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                GET_LOGO_FROM_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                GET_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    var bitmap: Bitmap? = null
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        viewBinding.imgMain.setImageBitmap(bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                GET_LOGO_FROM_GALLERY -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    var bitmap: Bitmap? = null
                    try {

                        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        viewBinding.logo.setImageBitmap(bitmap)
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

        viewBinding.llTextEdit.visibility = View.GONE
        viewBinding.llMain.visibility = View.VISIBLE

    }

    override fun setDefaultFont() {
        if (viewBinding.edtMain.isFocused) {
            viewBinding.edtMain.typeface = Typeface.DEFAULT
        }
        if (viewBinding.edtWeb.isFocused) {
            viewBinding.edtWeb.typeface = Typeface.DEFAULT
        }
    }

    override fun onFontChange(typeface: Typeface) {
        if (viewBinding.edtMain.isFocused) {
            viewBinding.edtMain.typeface = typeface
        }
        if (viewBinding.edtWeb.isFocused) {
            viewBinding.edtWeb.typeface = typeface
        }
    }


}


