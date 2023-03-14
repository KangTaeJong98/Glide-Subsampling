package com.photoview.test

import android.content.Context
import android.util.AttributeSet
import com.github.chrisbanes.photoview.PhotoView

class MyImageView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0,
) : PhotoView(context, attr, defStyle) {
}