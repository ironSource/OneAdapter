package com.idanatz.oneadapter.internal.selection

import android.view.MotionEvent
import org.jetbrains.annotations.NotNull
import androidx.annotation.Nullable
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.idanatz.oneadapter.internal.utils.extensions.toOneViewHolder

internal class OneItemDetailLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        return recyclerView.findChildViewUnder(e.x, e.y)?.let {
            val viewHolder = recyclerView.getChildViewHolder(it).toOneViewHolder()
            return viewHolder.createItemLookupInformation()
        }
    }
}