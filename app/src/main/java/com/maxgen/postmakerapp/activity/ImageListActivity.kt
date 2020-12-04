package com.maxgen.postmakerapp.activity

import android.content.res.AssetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        val files = assetManager.list("images")

        if (files != null) {
            for (file in files) {
                imageList!!.add(AssetModel("images/$file"))
            }
            imageAdapter = ImageListAdapter(imageList!!, this, this)
            viewBinding.rvImages.adapter = imageAdapter
            viewBinding.rvImages.layoutManager =
                LinearLayoutManager(this)
        }
    }

    override fun getLogoFromPostMaker(dirName: String) {
        TODO("Not yet implemented")
    }


}