package com.photoview.test

import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.photoview.test.databinding.EasyRegionActivityBinding
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

class EasyRegionActivity : AppCompatActivity() {
    private val binding by lazy { EasyRegionActivityBinding.inflate(layoutInflater) }

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
        Log.d("PASSZ", "Rect : $rectF")
        if (rectF.width() == 0F || rectF.height() == 0F) {
            return
        }

        val array = FloatArray(9)
        binding.imageView.imageMatrix.getValues(array)
        val transX = array[Matrix.MTRANS_X]
        val transY = array[Matrix.MTRANS_Y]
        val scaleX = array[Matrix.MSCALE_X]
        val scaleY = array[Matrix.MSCALE_Y]

        val bitmap = BitmapRegionDecoder.newInstance(assets.open("img0.png"))?.let {
            val left = abs(transX / scaleX)
            val top = abs(transY / scaleY)
            val right = abs(left + binding.imageView.measuredWidth / scaleX)
            val bottom = abs(top + binding.imageView.measuredHeight / scaleY)
            it.decodeRegion(Rect(ceil(left).toInt(), ceil(top).toInt(), ceil(right).toInt(), ceil(bottom).toInt()), BitmapFactory.Options())
        }

        binding.regionView.setImageBitmap(bitmap)
    }

    private fun getBitmap() = BitmapFactory.decodeStream(assets.open("img0.png"))
}