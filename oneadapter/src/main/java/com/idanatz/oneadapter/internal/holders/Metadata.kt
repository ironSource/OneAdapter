package com.idanatz.oneadapter.internal.holders

import com.idanatz.oneadapter.external.event_hooks.SwipeEventHook

data class Metadata(
		val position: Int = -1,
		val isRebinding: Boolean,
		val isFirst: Boolean,
		val isLast: Boolean,
		private val animationMetadata: AnimationMetadata? = null,
		private val selectionMetadata: SelectionMetadata? = null,
		private val swipeMetadata: SwipeMetadata? = null
) : AnimationMetadata, SelectionMetadata, SwipeMetadata {

	override val isAnimatingFirstBind: Boolean = animationMetadata?.isAnimatingFirstBind ?: false
	override val isSelected: Boolean = selectionMetadata?.isSelected ?: false
	override val swipeDirection: SwipeEventHook.SwipeDirection = swipeMetadata?.swipeDirection ?: SwipeEventHook.SwipeDirection.None
}

interface SelectionMetadata {
	val isSelected: Boolean
}

interface AnimationMetadata {
	val isAnimatingFirstBind: Boolean
}

interface SwipeMetadata {
	val swipeDirection: SwipeEventHook.SwipeDirection
}