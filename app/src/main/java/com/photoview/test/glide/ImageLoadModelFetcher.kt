package com.photoview.test.glide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.photoview.test.ImageLoadModel
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception

class ImageLoadModelFetcher(
    private val model: ImageLoadModel,
    private val loadData: ModelLoader.LoadData<File>
) : DataFetcher<Bitmap> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        loadData.fetcher.loadData(priority, object : DataFetcher.DataCallback<File> {
            override fun onDataReady(data: File?) {
                callback.onDataReady(data?.let(::getBitmap))
            }

            override fun onLoadFailed(e: Exception) {
                callback.onLoadFailed(e)
            }
        })
    }

    override fun cleanup() = loadData.fetcher.cleanup()

    override fun cancel() = loadData.fetcher.cancel()

    override fun getDataClass() = Bitmap::class.java

    override fun getDataSource() = DataSource.LOCAL

    private fun getBitmap(file: File): Bitmap? {
        val sizeOptions = getSizeOptions(file)
        val decodedBitmap = getDecodedBitmap(file)

        return decodedBitmap?.let {
            Bitmap.createScaledBitmap(it, sizeOptions.outWidth, sizeOptions.outHeight, false)
        }
    }

    private fun getSizeOptions(file: File) = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }.also { options ->
        FileInputStream(file).use { BitmapFactory.decodeStream(it, null, options) }
    }

    private fun getDecodedBitmap(file: File) = FileInputStream(file).use {
        BitmapFactory.decodeStream(it, null, getBitmapOptions())
    }

    private fun getBitmapOptions() = BitmapFactory.Options().apply {
        inSampleSize = if (model.scale >= 1.5F) {
            1
        } else {
            4
        }
    }
}