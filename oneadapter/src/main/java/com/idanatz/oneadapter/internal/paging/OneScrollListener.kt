package com.idanatz.oneadapter.internal.paging

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.idanatz.oneadapter.internal.utils.Logger
import com.idanatz.oneadapter.internal.utils.extensions.findLastItemIndex
import com.idanatz.oneadapter.internal.utils.extensions.findLastVisibleItemIndex
import com.idanatz.oneadapter.internal.utils.extensions.isScrolling

internal class OneScrollListener (
        private val layoutManager: RecyclerView.LayoutManager,
        private var visibleThreshold: Int, // The minimum amount of items to have below your current scroll position before loading more.
        private val loadMoreObserver: LoadMoreObserver,
        private val logger: Logger
) : RecyclerView.OnScrollListener() {

    private var currentPage = 0 // The current offset index of data you have loaded
    private var previousTotalItemCount = 0 // The total number of items in the data set after the last load
    private var loading = false // True if we are still waiting for the last set of data to load.
    private val startingPageIndex = 0 // Sets the starting page index

    init {
        when (layoutManager) {
            is GridLayoutManager -> visibleThreshold *= layoutManager.spanCount
            is StaggeredGridLayoutManager -> visibleThreshold *= layoutManager.spanCount
        }
        resetState()
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        if (!view.isScrolling) {
            return
        }

		handleLoadingEvent()
	}

	fun handleLoadingEvent() {
		if (!loadMoreObserver.shouldHandleLoadingEvent()) {
			return
		}

		when (evaluateLoadingState()) {
			LoadingState.FinishLoading -> {
				loading = false
				loadMoreObserver.onLoadingStateChanged(loading)
			}
			LoadingState.LoadingStarted -> {
				loading = true
				loadMoreObserver.onLoadingStateChanged(loading)
				currentPage++
				loadMoreObserver.onLoadMore(currentPage)
			}
			else -> { }
		}

		// update the previous item count to the current item count
		this.previousTotalItemCount = layoutManager.itemCount
	}

	fun resetState() {
        currentPage = startingPageIndex
        loading = false
        previousTotalItemCount = 0
    }

    private fun evaluateLoadingState(): LoadingState {
        val lastItemIndex = layoutManager.findLastItemIndex()
        val lastVisibleItemIndex = layoutManager.findLastVisibleItemIndex()

        return when {
			lastItemIndex == RecyclerView.NO_POSITION || lastVisibleItemIndex == RecyclerView.NO_POSITION -> LoadingState.Normal
            isLoadingFinished(lastItemIndex) -> LoadingState.FinishLoading
            shouldStartLoading(lastVisibleItemIndex, lastItemIndex) -> LoadingState.LoadingStarted
            loading -> LoadingState.MidLoading
            else -> LoadingState.Normal
        }.also {
            if (it != LoadingState.Normal) logger.logd { "onScrolled -> loading state: $it" }
        }
    }

    private fun shouldStartLoading(lastVisibleItemIndex: Int, lastItemIndex: Int) = !loading && lastVisibleItemIndex + visibleThreshold >= lastItemIndex
    private fun isLoadingFinished(lastItemIndex: Int) = loading && lastItemIndex > (previousTotalItemCount + 1) // + 1 for the loading holder

    enum class LoadingState {
        Normal, LoadingStarted, MidLoading, FinishLoading
    }
}