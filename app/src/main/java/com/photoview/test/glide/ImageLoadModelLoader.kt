package com.photoview.test.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import com.photoview.test.ImageLoadModel
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer

class ImageLoadModelLoader(
    private val modelLoader: ModelLoader<String, File>
) : ModelLoader<ImageLoadModel, Bitmap> {
    override fun buildLoadData(model: ImageLoadModel, width: Int, height: Int, options: Options): ModelLoader.LoadData<Bitmap>? {
        return modelLoader.buildLoadData(model.uri, width, height, options)?.let {
            ModelLoader.LoadData(
                ObjectKey(model),
                ImageLoadModelFetcher(model, it)
            )
        }
    }

    override fun handles(model: ImageLoadModel) = modelLoader.handles(model.uri)
}