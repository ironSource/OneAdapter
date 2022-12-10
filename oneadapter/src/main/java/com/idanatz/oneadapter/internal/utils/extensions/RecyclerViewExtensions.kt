package com.idanatz.oneadapter.internal.utils.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.idanatz.oneadapter.external.interfaces.Diffable
import com.idanatz.oneadapter.internal.holders.OneViewHolder
import java.lang.IllegalStateException

internal fun RecyclerView.LayoutManager.findFirstVisibleItemIndex(): Int {
    return when (this) {
        is LinearLayoutManager -> findFirstVisibleItemPosition()
        is GridLayoutManager -> findFirstVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val firstVisibleItemPositions = findFirstVisibleItemPositions(null)

            // get minimum element within the list
            var minSize = 0
            for (i in firstVisibleItemPositions.indices) {
                if (i == 0) {
                    minSize = firstVisibleItemPositions[i]
                } else if (firstVisibleItemPositions[i] < minSize) {
                    minSize = firstVisibleItemPositions[i]
                }
            }
            return minSize
        }
        else -> throw IllegalStateException("Recycler view layout manager is not supported")
    }
}

internal fun RecyclerView.LayoutManager.findLastVisibleItemIndex(): Int {
    return when (this) {
        is LinearLayoutManager -> findLastVisibleItemPosition()
        is GridLayoutManager -> findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val lastVisibleItemPositions = findLastVisibleItemPositions(null)

            // get maximum element within the list
            var maxSize = 0
            for (i in lastVisibleItemPositions.indices) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i]
                } else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i]
                }
            }
            return maxSize
        }
        else -> throw IllegalStateException("Recycler view layout manager is not supported")
    }
}

internal fun RecyclerView.LayoutManager.findLastItemIndex(): Int {
	return when (itemCount) {
		0 -> RecyclerView.NO_POSITION
		else -> itemCount - 1
	}
}

internal fun RecyclerView.ViewHolder.toOneViewHolder() = this as OneViewHolder<Diffable>

internal val RecyclerView.isScrolling: Boolean
	get() = scrollState != RecyclerView.SCROLL_STATE_IDLE