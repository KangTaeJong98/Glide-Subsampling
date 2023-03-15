package com.photoview.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import androidx.core.graphics.withSave
import com.github.chrisbanes.photoview.OnMatrixChangedListener
import com.github.chrisbanes.photoview.PhotoView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MyImageView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0,
) : PhotoView(context, attr, defStyle) {
    private val onMatrixChangedListenerList = ArrayList<OnMatrixChangedListener>()

    private val regionImageMatrix = Matrix()

    private var regionImageTranslateX = 0F
    private var regionImageTranslateY = 0F
    private var regionImageScale = 1F
    private var regionImageScaleX = 1F
    private var regionImageScaleY = 1F
    private var regionImageRectF = RectF()
    private var regionImage: Drawable? = null

    init {
        onMatrixChangedListenerList.add(
            OnMatrixChangedListener {
                if (regionImage != null) {
                    invalidate()
                }
            }
        )
    }

    fun setRegionImage(drawable: Drawable?) {
        regionImage = drawable
        if (drawable != null) {
            val widthDiff = if (displayRect.left > 0F) {
                displayRect.left
            } else {
                0F
            }
            val heightDiff = if (displayRect.top > 0F) {
                displayRect.top
            } else {
                0F
            }
            val scaleX = (measuredWidth - widthDiff * 2F) / drawable.intrinsicWidth
            val scaleY = (measuredHeight - heightDiff * 2F) / drawable.intrinsicHeight
            val maxScale = max(scaleX, scaleY)

            regionImageTranslateX = widthDiff
            regionImageTranslateY = heightDiff
            regionImageScale = scale
            regionImageScaleX = maxScale
            regionImageScaleY = maxScale
            regionImageRectF = RectF(displayRect)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        onDrawRegionImage(canvas)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setOnMatrixChangeListener { rectF ->
            onMatrixChangedListenerList.forEach {
                it.onMatrixChanged(rectF)
            }
        }
    }

    fun addOnMatrixChangeListener(listener: OnMatrixChangedListener) {
        onMatrixChangedListenerList.add(listener)
    }

    private fun onDrawRegionImage(canvas: Canvas) {
        val drawable = regionImage ?: return
        val x = if (displayRect.left <= 0F && regionImageRectF.left > 0F) {
            abs(regionImageTranslateX) * scale / regionImageScale
        } else {
            0F
        }
        val y = if (displayRect.top <= 0F && regionImageRectF.top > 0F) {
            abs(regionImageTranslateY) * scale / regionImageScale
        } else {
            0F
        }
        val widthPadding = if (displayRect.left > 0F) {
            displayRect.left
        } else {
            0F
        }
        val heightPadding = if (displayRect.top > 0F) {
            displayRect.top
        } else {
            0F
        }
        val translateX = if (widthPadding > 0F) {
            0F
        } else {
            displayRect.left - regionImageRectF.left / regionImageScale * scale
        }
        val translateY = if (heightPadding > 0F) {
            0F
        } else {
            displayRect.top - regionImageRectF.top / regionImageScale * scale
        }

        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        regionImageMatrix.reset()
        regionImageMatrix.postScale(regionImageScaleX * scale / regionImageScale, regionImageScaleY * scale / regionImageScale)
        regionImageMatrix.postTranslate(widthPadding, heightPadding)
        regionImageMatrix.postTranslate(translateX, translateY)
        regionImageMatrix.postTranslate(x, y)

        canvas.withSave {
            concat(regionImageMatrix)
            drawable.draw(this)
        }
    }
}