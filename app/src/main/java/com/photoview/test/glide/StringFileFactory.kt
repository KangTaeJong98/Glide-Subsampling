package com.photoview.test.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.photoview.test.ImageLoadModel
import java.io.File
import java.io.InputStream

class StringFileFactory : ModelLoaderFactory<String, File> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, File> {
        return StringFileLoader(multiFactory.build(String::class.java, InputStream::class.java))
    }

    override fun teardown() = Unit
}