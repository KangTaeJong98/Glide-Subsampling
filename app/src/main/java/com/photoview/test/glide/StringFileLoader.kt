package com.photoview.test.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import java.io.File
import java.io.InputStream

class StringFileLoader(
    private val loader: ModelLoader<String, InputStream>
) : ModelLoader<String, File> {
    override fun buildLoadData(model: String, width: Int, height: Int, options: Options): ModelLoader.LoadData<File>? {
        return loader.buildLoadData(model, width, height, options)?.let {
            ModelLoader.LoadData(
                it.sourceKey,
                it.alternateKeys,
                StringFileFetcher(it)
            )
        }
    }

    override fun handles(model: String) = loader.handles(model)
}