package com.photoview.test.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.photoview.test.ImageLoadModel
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer

class ImageLoadModelFactory : ModelLoaderFactory<ImageLoadModel, Bitmap> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<ImageLoadModel, Bitmap> {
        return ImageLoadModelLoader(
            multiFactory.build(String::class.java, File::class.java)
        )
    }

    override fun teardown() = Unit
}