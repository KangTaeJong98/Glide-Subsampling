package com.photoview.test.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.photoview.test.ImageLoadModel

class ModelToBitmapFactory(
    private val context: Context
) : ModelLoaderFactory<ImageLoadModel, Bitmap> {
    override fun build(multiFactory: MultiModelLoaderFactory) = ModelToBitmapLoader(context)

    override fun teardown() = Unit
}