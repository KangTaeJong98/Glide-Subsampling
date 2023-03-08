package com.photoview.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.photoview.test.databinding.HardRegionActivityBinding
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class HardRegionActivity : AppCompatActivity() {
    private val binding by lazy { HardRegionActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initImageView()
    }

    private fun initImageView() {
        binding.imageView.setOnMatrixChangeListener(::loadRegionBitmap)
        binding.imageView.setImageBitmap(getBitmap())
    }

    private fun loadRegionBitmap(rectF: RectF) {
        if (rectF.width() == 0F || rectF.height() == 0F) {
            return
        }

        val array = FloatArray(9)
        binding.imageView.imageMatrix.getValues(array)


        val bitmap = BitmapRegionDecoder.newInstance(assets.open("img0.png"))?.let {
            it.decodeRegion(
                Rect(
                    (it.width * getLeftPercent(array)).roundToInt(),
                    (it.height * getTopPercent(array)).roundToInt(),
                    (it.width * getRightPercent(array)).roundToInt(),
                    (it.height * getBottomPercent(array)).roundToInt()
                ), BitmapFactory.Options()
            )
        }

        binding.regionView.setImageBitmap(bitmap)
    }

    private fun getLeftPercent(array: FloatArray): Float {
        val transX = array[Matrix.MTRANS_X]
        val scaleX = array[Matrix.MSCALE_X]

        return if (transX > 0F) {
            0F
        } else {
            abs(transX / scaleX) / binding.imageView.drawable.intrinsicWidth
        }
    }

    private fun getTopPercent(array: FloatArray): Float {
        val transY = array[Matrix.MTRANS_Y]
        val scaleY = array[Matrix.MSCALE_Y]

        return if (transY > 0F) {
            0F
        } else {
            abs(transY / scaleY) / binding.imageView.drawable.intrinsicHeight
        }
    }

    private fun getRightPercent(array: FloatArray): Float {
        val transX = array[Matrix.MTRANS_X]
        val scaleX = array[Matrix.MSCALE_X]

        return if (transX > 0F) {
            1F
        } else {
            (abs(transX) + binding.imageView.measuredWidth) / scaleX / binding.imageView.drawable.intrinsicWidth
        }
    }

    private fun getBottomPercent(array: FloatArray): Float {
        val transY = array[Matrix.MTRANS_Y]
        val scaleY = array[Matrix.MSCALE_Y]

        return if (transY > 0F) {
            1F
        } else {
            (abs(transY) + binding.imageView.measuredHeight) / scaleY / binding.imageView.drawable.intrinsicHeight
        }
    }

    private fun getBitmap() = BitmapFactory.decodeStream(assets.open("img0.png"))
}