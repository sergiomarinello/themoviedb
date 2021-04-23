package com.smarinello.themoviedb.view.adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val SCROLL_STEP = 4

/**
 * Listener for [RecyclerView] to have the "infinite scrolling" behaviour.
 */
abstract class InfiniteScrollListener(
    private val layoutManager: LinearLayoutManager,
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val itemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        if (lastVisibleItemPosition == itemCount.minus(SCROLL_STEP)) {
            loadMore()
        }
    }

    /**
     * Callback for when more items should be loaded.
     */
    abstract fun loadMore()
}
