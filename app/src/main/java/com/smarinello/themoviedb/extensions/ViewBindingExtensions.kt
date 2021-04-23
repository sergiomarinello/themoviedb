package com.smarinello.themoviedb.extensions

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Bind adapter for view's visibility.
 */
@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}
