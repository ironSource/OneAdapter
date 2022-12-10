package com.idanatz.oneadapter.internal.holders

import android.animation.Animator
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.idanatz.oneadapter.external.event_hooks.EventHooksMap
import com.idanatz.oneadapter.external.event_hooks.SwipeEventHook
import com.idanatz.oneadapter.external.interfaces.Diffable
import com.idanatz.oneadapter.internal.selection.OneItemDetail
import com.idanatz.oneadapter.external.states.StatesMap
import com.idanatz.oneadapter.internal.utils.extensions.inflateLayout

internal abstract class OneViewHolder<M : Diffable>(
		parent: ViewGroup,
		@LayoutRes layoutResourceId: Int,
		firstBindAnimation: Animator? = null,
		val statesHooksMap: StatesMap<M>? = null, // Not all ViewHolders will have states configured
		val eventsHooksMap: EventHooksMap<M>? = null // Not all ViewHolders will have events configured
) : RecyclerView.ViewHolder(parent.context.inflateLayout(layoutResourceId, parent, false)) {

	lateinit var viewBinder: ViewBinder
	lateinit var metadata: Metadata
	lateinit var model: M

	val firstBindAnimation: Animator? = firstBindAnimation?.clone()

	// internal flags
	var isSwiping = false

	abstract fun onCreated()
	abstract fun onBind(model: M)
	abstract fun onUnbind(model: M)
	abstract fun onClicked(model: M)
	abstract fun onSwipe(canvas: Canvas, xAxisOffset: Float)
	abstract fun onSwipeComplete(model: M, swipeDirection: SwipeEventHook.SwipeDirection)
	abstract fun onSelected(model: M, selected: Boolean)

	fun onCreateViewHolder() {
		this.viewBinder = ViewBinder(itemView)
		handleEventHooks()
		onCreated()
	}

	fun onBindViewHolder(model: M,  metadata: Metadata) {
		this.model = model
		this.metadata = metadata

		handleAnimations()
		onBind(model)
	}

	fun onBindSelection(model: M, selected: Boolean) {
		if (statesHooksMap?.isSelectionStateConfigured() == true) {
			metadata = metadata.copy(
					selectionMetadata = object : SelectionMetadata {
						override val isSelected: Boolean = selected
					}
			)
			onSelected(model, selected)
		}
	}

	fun createItemLookupInformation(): OneItemDetail? {
		if (isSwiping) // while swiping disable the option to select
			return null

		return statesHooksMap?.getSelectionState()?.config?.let { selectionStateConfig ->
			if (selectionStateConfig.enabled) OneItemDetail(adapterPosition, itemId, selectionStateConfig.selectionTrigger)
			else null
		}
	}

	fun onSwipeViewHolder(canvas: Canvas, xAxisOffset: Float) {
		onSwipe(canvas, xAxisOffset)
	}

	fun onSwipeViewHolderComplete(swipeDirection: SwipeEventHook.SwipeDirection) {
		if (eventsHooksMap?.isSwipeEventHookConfigured() == true) {
			metadata = metadata.copy(
					swipeMetadata = object : SwipeMetadata {
						override val swipeDirection: SwipeEventHook.SwipeDirection = swipeDirection
					}
			)
			onSwipeComplete(model, swipeDirection)
		}
	}

	private fun handleAnimations() {
		if (metadata.isAnimatingFirstBind) {
			firstBindAnimation?.setTarget(itemView)
			firstBindAnimation?.start()
		} else {
			firstBindAnimation?.end()
		}
	}

	private fun handleEventHooks() {
		if (eventsHooksMap?.isClickEventHookConfigured() == true) {
			itemView.setOnClickListener {
				if (metadata.position != RecyclerView.NO_POSITION) {
					onClicked(model)
				}
			}
		}
	}
}