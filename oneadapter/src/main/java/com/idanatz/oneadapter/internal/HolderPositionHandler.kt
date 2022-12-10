package com.idanatz.oneadapter.internal

import android.util.SparseIntArray

internal class HolderPositionHandler(private var highestPositionMap: SparseIntArray = SparseIntArray()) {

    fun isFirstBind(viewType: Int, position: Int): Boolean {
        val highestPositionForType = highestPositionMap.get(viewType, -1)
        val shouldAnimateBind = position > highestPositionForType

        if (shouldAnimateBind) {
            highestPositionMap.put(viewType, position)
        }

        return shouldAnimateBind
    }

    fun resetState() {
        highestPositionMap.clear()
    }
}