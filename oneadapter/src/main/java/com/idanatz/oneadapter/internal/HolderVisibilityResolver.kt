package com.idanatz.oneadapter.internal

import android.graphics.Rect
import com.idanatz.oneadapter.external.interfaces.Diffable
import com.idanatz.oneadapter.internal.utils.extensions.findFirstVisibleItemIndex
import com.idanatz.oneadapter.internal.utils.extensions.findLastVisibleItemIndex
import com.idanatz.oneadapter.internal.validator.Validator

internal class HolderVisibilityResolver(private val internalAdapter: InternalAdapter) {

    fun <M : Diffable> getItems(ofClass: Class<M>, requiredVisibilityPercentage: Float = 1f): List<M> {
        return getIndexes(ofClass, requiredVisibilityPercentage).map { internalAdapter.data[it] as M }.toList()
    }

    fun <M : Diffable> getIndexes(ofClass: Class<M>, requiredVisibilityPercentage: Float = 1f): List<Int> {
        return getIndexes(requiredVisibilityPercentage).filter { ofClass.isInstance(internalAdapter.data[it]) }
    }

    fun getIndexes(requiredVisibilityPercentage: Float = 1f): List<Int> {
        val visibleIndexes = mutableListOf<Int>()
        val layoutManager = Validator.validateLayoutManagerExists(internalAdapter.recyclerView)

        for (i in layoutManager.findFirstVisibleItemIndex()..layoutManager.findLastVisibleItemIndex()) {
            val viewHolder = internalAdapter.recyclerView.findViewHolderForAdapterPosition(i) ?: continue

            // calculate visibility percentage
            var visibilityPercentage = 0f
            val viewMeasuredSurface = viewHolder.itemView.measuredHeight * viewHolder.itemView.measuredWidth

            // for any unexpected case that measured size not yet been calculated at this point (not suppose to happen)
            // we want to consider this view as "zero percentage" visible.
            if (viewMeasuredSurface > 0) {
                // get view's visible surface
                val rect = Rect()
                val isVisible = viewHolder.itemView.getGlobalVisibleRect(rect)
                if (isVisible) {
                    val viewVisibleSurface = (rect.height() * rect.width()).toFloat()
                    visibilityPercentage = viewVisibleSurface / viewMeasuredSurface
                }
            }

            if (visibilityPercentage >= requiredVisibilityPercentage) {
                visibleIndexes.add(i)
            }
        }
        return visibleIndexes
    }
}