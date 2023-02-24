package com.photoview.test

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.photoview.test.databinding.HolderImageBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

class ImageHolder(
    private val binding: HolderImageBinding
) : ViewHolder(binding.root) {
    constructor(parent: ViewGroup) : this(
        HolderImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    private val context get() = itemView.context
    private val lifecycleOwner get() = itemView.findViewTreeLifecycleOwner()

    private val translateX = MutableStateFlow(0F)
    private val translateY = MutableStateFlow(0F)
    private val scale = MutableStateFlow(1F)

    private var collectMatrixJob: Job? = null
    private var uiState: String = ""

    fun onBind(uiState: String) {
        this.uiState = uiState

        update()
    }

    fun onAttached() {
        collectMatrixJob?.cancel()
        collectMatrixJob = lifecycleOwner?.lifecycleScope?.launch {
            lifecycleOwner?.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(translateX, translateY, scale) { _, _, _ ->

                }.debounce(200L).collectLatest {
                    update()
                }
            }
        }
    }

    fun onDetached() {
        collectMatrixJob?.cancel()
        collectMatrixJob = null
    }

    private fun update() {
        Glide.with(binding.imageView)
            .load(ImageLoadModel(uri = uiState, scale = scale.value))
//            .load(uiState)
            .into(
                object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.imageView.setOnMatrixChangeListener(null)
                        binding.imageView.setImageDrawable(resource)
                        binding.imageView.setDisplayMatrix(
                            Matrix().apply {
                                postScale(scale.value, scale.value)
                                postTranslate(translateX.value, translateY.value)
                            }
                        )

                        binding.imageView.setOnMatrixChangeListener {
                            translateX.value = it.left
                            translateY.value = it.top
                            scale.value = binding.imageView.scale
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                }
            )

    }
}