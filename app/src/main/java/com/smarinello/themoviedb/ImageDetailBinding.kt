package com.smarinello.themoviedb

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL

/**
 * Object responsible to binding the [ImageView] to [R.layout.activity_detail]
 */
object ImageDetailBinding {
    @JvmStatic
    @BindingAdapter("android:src")
    fun loadImage(imageView: ImageView, url: String?) {
        url?.let {
            Glide.with(imageView)
                .load(it)
                .priority(Priority.HIGH)
                .centerInside()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(SIZE_ORIGINAL)
                .apply(
                    RequestOptions().placeholder(
                        R.drawable.ic_image_placeholder
                    )
                )
                .into(imageView)
        }
    }
}
