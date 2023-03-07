package com.photoview.test

import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.photoview.test.databinding.EasyRegionActivityBinding
import kotlin.math.abs
import kotlin.math.ceil

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
        val measuredWidth = binding.imageView.measuredWidth
        val measuredHeight = binding.imageView.measuredHeight
        val leftPercent = abs(rectF.left / measuredWidth / binding.imageView.scale)
        val topPercent = abs(rectF.top / measuredHeight / binding.imageView.scale)
        val rightPercent = abs(leftPercent + (1 / binding.imageView.scale))
        val bottomPercent = abs(topPercent + (1 / binding.imageView.scale))

        val bitmap = BitmapRegionDecoder.newInstance(assets.open("img0.png"))?.let {
            val rect = Rect(
                ceil(it.width * leftPercent).toInt(),
                ceil(it.height * topPercent).toInt(),
                ceil(it.width * rightPercent).toInt(),
                ceil(it.height * bottomPercent).toInt()
            )

            if (rect.width() == 0 || rect.height() == 0) {
                null
            } else {
                it.decodeRegion(rect, BitmapFactory.Options())
            }
        }

        binding.regionView.setImageBitmap(bitmap)
    }

    private fun getBitmap() = BitmapFactory.decodeStream(assets.open("img0.png"))
}