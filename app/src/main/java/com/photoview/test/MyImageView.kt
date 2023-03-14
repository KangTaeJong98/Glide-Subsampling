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
//        onDrawDebug(canvas)
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

    fun removeOnMatrixChangeListener(listener: OnMatrixChangedListener) {
        onMatrixChangedListenerList.remove(listener)
    }

    private fun onDrawRegionImage(canvas: Canvas) {
        val drawable = regionImage ?: return
        val x = if (displayRect.left > 0) {
            0F
        } else {
            (abs(regionImageRectF.left) - displayRect.left).coerceAtLeast(0F)
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
        val scaleDiff = (scale - regionImageScale)

        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)


        regionImageMatrix.reset()
        regionImageMatrix.postScale(regionImageScaleX * scale / regionImageScale, regionImageScaleY * scale / regionImageScale)
        regionImageMatrix.postTranslate(widthPadding, heightPadding)
        regionImageMatrix.postTranslate(translateX, translateY)


        Log.d("PASSZ", "View : $scale, $measuredWidth, $measuredHeight")
        Log.d("PASSZ", "Rect : $displayRect")
        Log.d("PASSZ", "Region : $regionImageScale, ${drawable.intrinsicWidth}, ${drawable.intrinsicHeight}")
        Log.d("PASSZ", "Region Rect: $regionImageRectF")

        canvas.withSave {
            concat(regionImageMatrix)
            drawable.draw(this)
        }
    }

    private fun onDrawDebug(canvas: Canvas) {
        val drawable = ColorDrawable(Color.RED)
        val matrix = Matrix()
        val w = if (displayRect.left > 0F) {
            displayRect.left
        } else {
            (displayRect.left - regionImageRectF.left)
        }
        val h = if (displayRect.top > 0F) {
            displayRect.top
        } else {
            (displayRect.top - regionImageRectF.top)
        }

        drawable.setBounds(0, 0, 300, 300)
//        matrix.postScale(measuredWidth / 300F, w, measuredHeight / 300F, h)
//        matrix.postTranslate(w, h)
        matrix.postScale(scale, scale)

        canvas.withSave {
            concat(matrix)
            drawable.draw(this)
        }
    }
}