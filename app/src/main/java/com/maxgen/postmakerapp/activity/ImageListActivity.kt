package com.maxgen.postmakerapp.activity

import android.content.Intent
import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.maxgen.postmakerapp.adapter.ImageListAdapter
import com.maxgen.postmakerapp.adapter.OnChooseLogoListener
import com.maxgen.postmakerapp.databinding.ActivityImageListBinding
import com.maxgen.postmakerapp.model.AssetModel

class ImageListActivity : AppCompatActivity(), OnChooseLogoListener {
    private lateinit var viewBinding: ActivityImageListBinding
    private var imageAdapter: ImageListAdapter? = null
    private var imageList: ArrayList<AssetModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityImageListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        loadImages()
    }

    private fun loadImages() {
        imageList = ArrayList()
        val assetManager: AssetManager = this.resources.assets

        val files = assetManager.list("background")

        if (files != null) {
            for (file in files) {
                imageList!!.add(AssetModel("background/$file"))
            }
            imageAdapter = ImageListAdapter(imageList!!, this, this)
            viewBinding.rvImages.adapter = imageAdapter
            viewBinding.rvImages.layoutManager =
                GridLayoutManager(this, 2)
        }
    }

    override fun getLogoFromPostMaker(byteArray: ByteArray) {
        val intent = Intent()
        intent.putExtra("image", byteArray)
        setResult(RESULT_OK, intent)
        finish()
    }

}