package com.idanatz.oneadapter

import androidx.recyclerview.widget.RecyclerView
import com.idanatz.oneadapter.external.interfaces.Diffable
import com.idanatz.oneadapter.external.modules.*
import com.idanatz.oneadapter.internal.InternalAdapter
import com.idanatz.oneadapter.external.modules.Modules
import com.idanatz.oneadapter.internal.utils.extensions.*
import com.idanatz.oneadapter.internal.utils.extensions.getIndexOfItem
import com.idanatz.oneadapter.external.MissingModuleDefinitionException
import com.idanatz.oneadapter.external.MultipleModuleConflictException
import com.idanatz.oneadapter.external.UnsupportedClassException

class OneAdapter(recyclerView: RecyclerView) {

    internal val internalAdapter = InternalAdapter(recyclerView)

    private val internalItems: List<Diffable>
        get() = internalAdapter.data

    val modules: Modules
        get() = internalAdapter.modules

    val itemCount: Int
        get() = internalAdapter.itemCount


    constructor(recyclerView: RecyclerView, block: OneAdapterDslBuilder.() -> Unit) : this(recyclerView) {
        OneAdapterDslBuilder().apply(block).build()
    }

    /**
     * Sets the adapter's item list to the given list.
     * @throws MissingModuleDefinitionException if any of the given items are missing an ItemModule.
     */
    fun setItems(items: List<Diffable>) {
        internalAdapter.updateData(items.createMutableCopy())
    }

    /**
     * Sets the adapter's item list to an empty list.
     * Will trigger an EmptinessModule's onBind call if configured.
     * @see EmptinessModule
     */
    fun clear() {
        internalAdapter.updateData(mutableListOf())
    }

    /**
     * Appends the specified item to the end of the adapter's list.
     * @throws MissingModuleDefinitionException if any of the given items are missing an ItemModule.
     */
    fun add(item: Diffable) {
        add(internalItems.size, item)
    }

    /**
     * Inserts the specified item at the specified position to the adapter's list.
     * Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
     * @param index - index at which the specified item is to be inserted
     * @param item - item to be inserted
     * @throws MissingModuleDefinitionException if any of the given items are missing an ItemModule.
     */
    fun add(index: Int, item: Diffable) {
        val modifiedList = internalItems.createMutableCopyAndApply { add(index, item) }
        internalAdapter.updateData(modifiedList)
    }

    /**
     * Appends the specified items list to the end of the adapter's list.
     * @throws MissingModuleDefinitionException if any of the given items are missing an ItemModule.
     */
    fun add(items: List<Diffable>) {
        val modifiedList = internalItems.createMutableCopyAndApply { addAll(items) }
        internalAdapter.updateData(modifiedList)
    }

    fun remove(index: Int) {
        val modifiedList = internalItems.createMutableCopyAndApply { removeAt(index) }
        internalAdapter.updateData(modifiedList)
    }

    fun remove(item: Diffable) {
        val indexToRemove = internalItems.getIndexOfItem(item)
        if (indexToRemove != -1) {
            remove(indexToRemove)
        }
    }

    fun remove(items: List<Diffable>) {
        val modifiedList = internalItems.createMutableCopyAndApply { removeAllItems(items) }
        internalAdapter.updateData(modifiedList)
    }

    fun update(index: Int) {
        if (index in 0 until itemCount) {
            internalAdapter.notifyItemChanged(index)
        }
    }

    fun update(item: Diffable) {
        val indexToSet = internalItems.getIndexOfItem(item)
        if (indexToSet != -1) {
            val modifiedList = internalItems.createMutableCopyAndApply { set(indexToSet, item) }
            internalAdapter.updateData(modifiedList)
        }
    }

    fun update(items: List<Diffable>) {
        val modifiedList = internalItems.createMutableCopyAndApply { updateAllItems(items) }
        internalAdapter.updateData(modifiedList)
    }

    /**
     * Attach the given ItemModule to the adapter.
     * This will add the ability to process items of the ItemModule type.
     * @throws MultipleModuleConflictException if an ItemModule of the same type already exists.
     */
    fun <M : Diffable> attachItemModule(itemModule: ItemModule<out M>): OneAdapter {
        internalAdapter.register(itemModule)
        return this
    }

    fun <M : Diffable> attachItemModules(vararg itemModules: ItemModule<out M>): OneAdapter {
        itemModules.forEach { attachItemModule(it) }
        return this
    }

    fun attachPagingModule(pagingModule: PagingModule): OneAdapter {
        internalAdapter.enablePaging(pagingModule)
        return this
    }

    fun attachEmptinessModule(emptinessModule: EmptinessModule): OneAdapter {
        internalAdapter.enableEmptiness(emptinessModule)
        return this
    }

    fun attachItemSelectionModule(itemSelectionModule: ItemSelectionModule): OneAdapter {
        internalAdapter.enableSelection(itemSelectionModule)
        return this
    }

    @JvmOverloads
    fun getVisibleItemIndexes(requiredVisibilityPercentage: Float = 1f): List<Int> {
        return internalAdapter.holderVisibilityResolver.getIndexes(requiredVisibilityPercentage)
    }

    @JvmOverloads
    fun <M : Diffable> getVisibleItemIndexes(ofClass: Class<M>, requiredVisibilityPercentage: Float = 1f): List<Int> {
        return internalAdapter.holderVisibilityResolver.getIndexes(ofClass, requiredVisibilityPercentage)
    }

    @JvmOverloads
    fun <M : Diffable> getVisibleItems(ofClass: Class<M>, requiredVisibilityPercentage: Float = 1f): List<M> {
        return internalAdapter.holderVisibilityResolver.getItems(ofClass, requiredVisibilityPercentage)
    }

    /**
     * Retrieves the view type of an item with a given position.
     * @throws IndexOutOfBoundsException if the position is >= itemCount
     */
    fun getItemViewType(position: Int): Int {
        return internalAdapter.getItemViewType(position)
    }

    /**
     * Retrieves the view type associated with the items of the given class.
     * @throws MissingModuleDefinitionException if the class is not registered as an Module data type.
     */
    fun <M : Diffable> getItemViewTypeFromClass(ofClass: Class<M>): Int {
        return internalAdapter.getItemViewTypeFromClass(ofClass)
    }

	/**
	 * Returns the index of the first occurrence of the specified item in the adapter, or -1 if the specified
	 * item is not contained in the adapter.
	 */
	fun getItemPosition(item: Diffable): Int = internalAdapter.getItemPosition(item)

	fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
		internalAdapter.registerAdapterDataObserver(observer)
	}

	fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
		internalAdapter.unregisterAdapterDataObserver(observer)
	}

    inner class OneAdapterDslBuilder {
        var itemModules = ItemModulesMap()
        var pagingModule: PagingModule? = null
        var emptinessModule: EmptinessModule? = null
        var itemSelectionModule: ItemSelectionModule? = null

        fun build() {
            attachItemModules(*itemModules.values.toTypedArray())
            pagingModule?.let { attachPagingModule(it) }
            emptinessModule?.let { attachEmptinessModule(it) }
            itemSelectionModule?.let { attachItemSelectionModule(it) }
        }

        inner class ItemModulesMap : HashMap<String, ItemModule<out Diffable>>() {
            operator fun plusAssign(itemModule: ItemModule<out Diffable>) {
                put(itemModule.javaClass.name, itemModule)
            }
        }
    }
}