package com.photoview.test.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.photoview.test.ImageLoadModel
import java.io.InputStream

class ModelToBitmapLoader(
    private val context: Context
) : ModelLoader<ImageLoadModel, Bitmap> {
    override fun buildLoadData(model: ImageLoadModel, width: Int, height: Int, options: Options) = ModelLoader.LoadData(
        ObjectKey(model),
        ModelToBitmapFetcher(context, model, width, height, options)
    )

    override fun handles(model: ImageLoadModel) = true
}