package com.photoview.test

import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
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
import kotlin.math.max
import kotlin.math.min

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
        val measuredWidth = binding.imageView.measuredWidth
        val measuredHeight = binding.imageView.measuredHeight

        val zz = ((measuredWidth * binding.imageView.scale - rectF.width()) / 2)
        val zzz = zz / (measuredWidth * binding.imageView.scale / rectF.width())
        val leftPercent = abs((rectF.left) / measuredWidth / binding.imageView.scale)
        val topPercent = abs(rectF.top / measuredHeight / binding.imageView.scale)
        val rightPercent = abs(leftPercent + (1 / binding.imageView.scale))
        val bottomPercent = abs(topPercent + (1 / binding.imageView.scale))

        Log.d("PASSZ", "zz : $zz")
        Log.d("PASSZ", "zzz : $zzz")
        Log.d("PASSZ", "View : $measuredWidth, $measuredHeight")
        Log.d("PASSZ", "Rect : $rectF")
        Log.d("PASSZ", "View Scale : ${binding.imageView.scale}")
        Log.d("PASSZ", "Rect Scale : ${min(binding.imageView.measuredWidth / rectF.width(), binding.imageView.measuredHeight / rectF.height())}")
        Log.d("PASSZ", "Left : ${String.format("%.2f", leftPercent)}")
        Log.d("PASSZ", "Top : ${String.format("%.2f", topPercent)}")
        Log.d("PASSZ", "Right : ${String.format("%.2f", rightPercent)}")
        Log.d("PASSZ", "Bottom : ${String.format("%.2f", bottomPercent)}")
        val bitmap = BitmapRegionDecoder.newInstance(assets.open("img0.png"))?.let {
            Log.d("PASSZ", "Size : ${it.width}, ${it.height}")
            val rect = Rect(
                ceil(it.width * leftPercent).toInt(),
                ceil(it.height * topPercent).toInt(),
                it.width,
                it.height
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