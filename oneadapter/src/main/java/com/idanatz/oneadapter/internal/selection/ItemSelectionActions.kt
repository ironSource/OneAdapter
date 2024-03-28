package com.idanatz.oneadapter.internal.selection

import com.idanatz.oneadapter.external.interfaces.Diffable

interface ItemSelectionActions {

    fun startSelection()
	fun select(position: Int): Boolean?
	fun selectAll(): Boolean?
    fun clearSelection(): Boolean?

    fun getSelectedItems(): List<Diffable>
    fun getSelectedPositions(): List<Int>

    /**
     * returns True if selection is currently active after calling startSelection or by selecting an item, otherwise false.
     */
    fun isSelectionActive(): Boolean
    fun isPositionSelected(position: Int): Boolean

    fun removeSelectedItems()
}