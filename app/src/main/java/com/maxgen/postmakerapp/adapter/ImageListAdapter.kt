package com.maxgen.postmakerapp.adapter

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maxgen.postmakerapp.R
import com.maxgen.postmakerapp.model.AssetModel
import java.io.ByteArrayOutputStream
import java.io.InputStream


class ImageListAdapter(
    private val imageList: ArrayList<AssetModel>,
    private val context: Context,
    private val listener: OnChooseLogoListener
) : RecyclerView.Adapter<ImageListAdapter.ImageViewModel>() {
    class ImageViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewModel {
        return ImageViewModel(
            LayoutInflater.from(parent.context).inflate(R.layout.image_list_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewModel, position: Int) {
        val assetManager: AssetManager = context.assets

        val inputStream: InputStream = assetManager.open(imageList[position].dirName)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        Glide.with(context).load(bitmap).into(holder.imageView)

        holder.itemView.setOnClickListener {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            listener.getLogoFromPostMaker(byteArray)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}