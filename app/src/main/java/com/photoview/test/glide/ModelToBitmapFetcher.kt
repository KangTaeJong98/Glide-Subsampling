package com.photoview.test.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.photoview.test.ImageLoadModel
import java.io.File
import java.io.FileInputStream
import kotlin.math.min

class ModelToBitmapFetcher(
    private val context: Context,
    private val model: ImageLoadModel,
    private val width: Int,
    private val height: Int,
    private val options: Options
) : DataFetcher<Bitmap> {
    private var target: Target<File>? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        target = Glide.with(context)
            .asFile()
            .load(model.uri)
            .into(
                object : CustomTarget<File>() {
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        callback.onDataReady(getBitmap(resource))
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        callback.onLoadFailed(RuntimeException(""))
                    }
                }
            )
    }

    private fun getBitmap(file: File): Bitmap? {
        val originSizeOptions = getOriginSizeOptions(file)
        val decodedBitmap = getDecodedBitmap(file, originSizeOptions)

        return decodedBitmap?.let { Bitmap.createScaledBitmap(it, originSizeOptions.outWidth, originSizeOptions.outHeight, false) }
    }

    private fun getOriginSizeOptions(file: File) = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }.also {
        BitmapFactory.decodeStream(FileInputStream(file), null, it)
    }

    private fun getDecodedBitmap(file: File, originSizeOptions: BitmapFactory.Options) = BitmapFactory.decodeStream(
        FileInputStream(file),
        null,
        BitmapFactory.Options().apply {
            inSampleSize = getSampleSize(originSizeOptions)
        }
    )

    private fun getSampleSize(originSizeOptions: BitmapFactory.Options) = if (model.scale >= 1.5) {
        1
    } else {
        val widthScale = (originSizeOptions.outWidth / width).coerceAtLeast(1)
        val heightScale = (originSizeOptions.outHeight / height).coerceAtLeast(1)

        min(widthScale, heightScale)
    }


    override fun cleanup() = Unit

    override fun cancel() {
        target?.request?.clear()
    }

    override fun getDataClass() = Bitmap::class.java

    override fun getDataSource() = DataSource.DATA_DISK_CACHE
}