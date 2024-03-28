package com.idanatz.oneadapter.internal.selection

import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.external.modules.ItemSelectionModule
import com.idanatz.oneadapter.external.modules.ItemSelectionModuleConfig
import com.idanatz.oneadapter.external.states.SelectionStateConfig
import com.idanatz.oneadapter.internal.utils.extensions.toOneViewHolder
import java.util.*

internal class OneSelectionHandler(
	selectionModule: ItemSelectionModule,
	val recyclerView: RecyclerView,
	val getItemModuleByItemId: (Long) -> ItemModule<*>?
) : SelectionTracker.SelectionObserver<Long>() {

	private val ghostKey = UUID.randomUUID().mostSignificantBits
	private var previousSelectionCount: Int = 0

	private val itemKeyProvider: OneItemKeyProvider = OneItemKeyProvider(recyclerView)
	private val selectionTracker: SelectionTracker<Long> = SelectionTracker.Builder(
			recyclerView.id.toString(),
			recyclerView,
			itemKeyProvider,
			OneItemDetailLookup(recyclerView),
			StorageStrategy.createLongStorage()
	)
	.withSelectionPredicate(object : SelectionTracker.SelectionPredicate<Long>() {
		override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
			if (key == ghostKey)
				return true // always accept the ghost key

			val itemModule = getItemModuleByItemId(key) ?: return false

			val isEnabled = itemModule.states.getSelectionState()?.config?.enabled ?: false
			val forbidDueToManualSelection = itemModule.states.getSelectionState()?.config?.selectionTrigger == SelectionStateConfig.SelectionTrigger.Manual && !isInManualSelection()
			return isEnabled && !forbidDueToManualSelection
		}

		override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean = true

		override fun canSelectMultiple(): Boolean = when (selectionModule.config.selectionType) {
			ItemSelectionModuleConfig.SelectionType.Single -> false
			ItemSelectionModuleConfig.SelectionType.Multiple -> true
		}
	})
	.build()
	.also { it.addObserver(this) }

	var observer: SelectionObserver? = null

	fun startSelection() {
		selectionTracker.select(ghostKey)
	}

	fun select(position: Int): Boolean {
		val key = itemKeyProvider.getKey(position) ?: return false
		return selectionTracker.select(key)
	}

	fun selectAll(): Boolean {
		val itemCount = recyclerView.adapter?.itemCount ?: 0
		val toBeSelectedKeys = arrayListOf<Long>()
		for (i in 0 until itemCount) {
			val key = itemKeyProvider.getKey(i)
			if (key != null && !selectionTracker.isSelected(key)) {
				toBeSelectedKeys.add(key)
			}

		}
		return selectionTracker.setItemsSelected(toBeSelectedKeys, true)
	}

	fun clearSelection(): Boolean = selectionTracker.clearSelection()

	fun getSelectedPositions(): List<Int> {
		return selectionTracker.selection?.map { key -> itemKeyProvider.getPosition(key) }?.filter { it >= 0 } ?: emptyList()
	}

	fun inSelectionActive(): Boolean {
		return selectionTracker.selection.size() != 0
	}

	fun isPositionSelected(position: Int): Boolean {
		return selectionTracker.isSelected(itemKeyProvider.getKey(position))
	}

	override fun onItemStateChanged(key: Long, selected: Boolean) {
		recyclerView.findViewHolderForItemId(key)?.toOneViewHolder()?.let { holder ->
			observer?.onItemStateChanged(holder, itemKeyProvider.getPosition(key), selected)
		}
	}

	override fun onSelectionChanged() {
		val currentSelectionCount = selectionTracker.selection.size()

		when {
			previousSelectionCount == 0 && currentSelectionCount > 0 -> observer?.onSelectionStarted()
			previousSelectionCount > 0 && currentSelectionCount == 0 -> observer?.onSelectionEnded()
		}

		// ghost key is not an actual item but a trick to start manual selection
		// no need to include it in the selection count
		val countToNotify =
				if (isInManualSelection()) currentSelectionCount - 1
				else currentSelectionCount
		observer?.onSelectionUpdated(countToNotify)

		previousSelectionCount = currentSelectionCount
	}

	private fun isInManualSelection() = selectionTracker.isSelected(ghostKey)
}