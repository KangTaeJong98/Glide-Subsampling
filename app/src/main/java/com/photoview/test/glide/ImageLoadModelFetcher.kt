package com.photoview.test.glide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import java.nio.ByteBuffer

class ImageLoadModelFetcher(
    private val model: ImageLoadModel,
    private val loadData: ModelLoader.LoadData<InputStream>
) : DataFetcher<Bitmap> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        loadData.fetcher.loadData(priority, object : DataFetcher.DataCallback<InputStream> {
            override fun onDataReady(data: InputStream?) {
                callback.onDataReady(data?.let(InputStream::buffered)?.let(::getBitmap))
                data?.close()
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

    private fun getBitmap(inputStream: BufferedInputStream): Bitmap? {
        inputStream.mark(Int.MAX_VALUE)
        val sizeOptions = getSizeOptions(inputStream)
        inputStream.reset()
        val decodedBitmap = getDecodedBitmap(inputStream)

        return decodedBitmap?.let {
            Bitmap.createScaledBitmap(it, sizeOptions.outWidth, sizeOptions.outHeight, false)
        }
    }

    private fun getSizeOptions(inputStream: BufferedInputStream) = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }.also {
        BitmapFactory.decodeStream(inputStream, null, it)
    }

    private fun getDecodedBitmap(inputStream: InputStream) = BitmapFactory.decodeStream(inputStream, null, getBitmapOptions())

    private fun getBitmapOptions() = BitmapFactory.Options().apply {
        inSampleSize = if (model.scale >= 1.5F) {
            1
        } else {
            4
        }
    }
}