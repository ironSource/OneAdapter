package com.idanatz.sample.examples.complete

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.idanatz.sample.models.HeaderModel
import com.idanatz.sample.models.MessageModel
import com.idanatz.oneadapter.OneAdapter
import com.idanatz.oneadapter.external.event_hooks.ClickEventHook
import com.idanatz.oneadapter.external.states.SelectionState
import com.idanatz.oneadapter.sample.R
import com.bumptech.glide.Glide
import com.idanatz.oneadapter.external.event_hooks.SwipeEventHook
import com.idanatz.oneadapter.external.modules.*
import com.idanatz.oneadapter.external.modules.ItemSelectionModuleConfig.*
import com.idanatz.oneadapter.external.states.SelectionStateConfig
import com.idanatz.sample.examples.BaseExampleActivity
import com.idanatz.sample.examples.ActionsDialog.*
import com.idanatz.sample.models.StoriesModel
import com.idanatz.sample.models.StoryModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class CompleteExampleActivity : BaseExampleActivity() {

    companion object {
        const val ICON_MARGIN = 50
    }

    private lateinit var viewModel: CompleteExampleViewModel
    private lateinit var oneAdapter: OneAdapter
    private lateinit var compositeDisposable: CompositeDisposable
    private var toolbarMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this).get(CompleteExampleViewModel::class.java)

        oneAdapter = OneAdapter(recyclerView) {
            itemModules += StoriesItem()
            itemModules += HeaderItem()
            itemModules += MessageItem()
            emptinessModule = EmptinessModuleImpl()
            pagingModule = PagingModuleImpl()
            itemSelectionModule = ItemSelectionModuleImpl()
        }

        initActionsDialog(*Action.values()).setListener(viewModel)

        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onBackPressed() {
        if (oneAdapter.modules.itemSelectionModule?.actions?.isSelectionActive() == true) {
            oneAdapter.modules.itemSelectionModule?.actions?.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

    private fun observeViewModel() {
        compositeDisposable.add(
                viewModel.itemsSubject
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { if (it.isNotEmpty()) toolbarMenu?.findItem(R.id.action_start_selection)?.isVisible = true }
                        .subscribe { items -> oneAdapter.setItems(items) }
        )
    }

    private inner class HeaderItem : ItemModule<HeaderModel>() {
        init {
            config {
                layoutResource = R.layout.header_model
                // can be implemented by constructing ObjectAnimator
                firstBindAnimation = ObjectAnimator().apply {
                    propertyName = "translationX"
                    setFloatValues(-1080f, 0f)
                    duration = 750
                }
            }
            onBind { model, viewBinder, _ ->
                val headerTitle = viewBinder.findViewById<TextView>(R.id.header_title)
                val headerSwitch = viewBinder.findViewById<SwitchCompat>(R.id.header_switch)

                headerTitle.text = model.name
                headerSwitch.visibility = if (model.checkable) View.VISIBLE else View.GONE
                headerSwitch.isChecked = model.checked
                headerSwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.headerCheckedChanged(model, isChecked) }
            }
        }
    }

    private inner class MessageItem : ItemModule<MessageModel>() {
        init {
            config {
                layoutResource = R.layout.message_model
                // can be implemented by inflating Animator Xml
                firstBindAnimation = AnimatorInflater.loadAnimator(this@CompleteExampleActivity, R.animator.item_animation_example)
            }
            onBind { model, viewBinder, metadata ->
                val id = viewBinder.findViewById<TextView>(R.id.id)
                val title = viewBinder.findViewById<TextView>(R.id.title)
                val body = viewBinder.findViewById<TextView>(R.id.body)
                val avatarImage = viewBinder.findViewById<ImageView>(R.id.avatarImage)
                val selectedLayer = viewBinder.findViewById<ImageView>(R.id.selected_layer)

                id.text = getString(R.string.message_model_id).format(model.id)
                title.text = model.title
                body.text = model.body
                Glide.with(viewBinder.rootView).load(model.avatarImageId).into(avatarImage)

                // selected UI
                avatarImage.alpha = if (metadata.isSelected) 0.5f else 1f
                selectedLayer.visibility = if (metadata.isSelected) View.VISIBLE else View.GONE
                viewBinder.rootView.setBackgroundColor(if (metadata.isSelected) ContextCompat.getColor(this@CompleteExampleActivity, R.color.light_gray) else Color.WHITE)
            }
            eventHooks += ClickEventHook<MessageModel>().apply {
                onClick { model, viewBinder, _ ->
                    Toast.makeText(viewBinder.rootView.context, "${model.title} clicked", Toast.LENGTH_SHORT).show()
                }
            }
            eventHooks += SwipeEventHook<MessageModel>().apply {
                config {
                    swipeDirection = listOf(SwipeEventHook.SwipeDirection.Start, SwipeEventHook.SwipeDirection.End)
                }
                onSwipe { canvas, xAxisOffset, viewBinder ->
                    when {
                        xAxisOffset < 0 -> paintSwipeLeft(canvas, xAxisOffset, viewBinder.rootView)
                        xAxisOffset > 0 -> paintSwipeRight(canvas, xAxisOffset, viewBinder.rootView)
                    }
                }
                onSwipeComplete { model, _, metadata ->
                    when (metadata.swipeDirection) {
                        SwipeEventHook.SwipeDirection.Start -> viewModel.onSwipeToDeleteItem(model)
                        SwipeEventHook.SwipeDirection.End -> {
                            Toast.makeText(this@CompleteExampleActivity, "${model.title} snoozed", Toast.LENGTH_SHORT).show()
                            oneAdapter.update(metadata.position) // for resetting the view back into place
                        }
                    }
                }
            }
            states += SelectionState<MessageModel>().apply {
                onSelected { model, selected ->
                    val message = "${model.title} " + if (selected) "selected" else "unselected"
                    Toast.makeText(this@CompleteExampleActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private class StoriesItem : ItemModule<StoriesModel>() {
        private var oneAdapter: OneAdapter? = null

        init {
            config {
                layoutResource = R.layout.recycler_view
            }
            onCreate { viewBinder ->
                val nestedRecyclerView = viewBinder.findViewById<RecyclerView>(R.id.recycler)
                val layoutManager = LinearLayoutManager(viewBinder.rootView.context, LinearLayoutManager.HORIZONTAL, false)
                nestedRecyclerView.layoutManager = layoutManager

                oneAdapter = OneAdapter(nestedRecyclerView)
                        .attachItemModule(StoryItem())
            }
            onBind { model, viewBinder, _ ->
                oneAdapter?.setItems(model.stories)

                // restore scroll state
                val nestedRecyclerView = viewBinder.findViewById<RecyclerView>(R.id.recycler)
                val layoutManager = nestedRecyclerView.layoutManager as LinearLayoutManager?
                layoutManager?.onRestoreInstanceState(model.scrollPosition)
            }
            onUnbind { model, viewBinder, _ ->
                // save scroll state
                val nestedRecyclerView = viewBinder.findViewById<RecyclerView>(R.id.recycler)
                val layoutManager = nestedRecyclerView.layoutManager as LinearLayoutManager?
                model.scrollPosition = layoutManager?.onSaveInstanceState()
            }
        }

        private class StoryItem : ItemModule<StoryModel>() {
            init {
                config {
                    layoutResource = R.layout.story_model
                }
                onBind { model, viewBinder, _ ->
                    val story = viewBinder.findViewById<ImageView>(R.id.story)
                    Glide.with(viewBinder.rootView).load(model.storyImageId).into(story)
                }
            }
        }
    }

    private class EmptinessModuleImpl : EmptinessModule() {
        init {
            config {
                layoutResource = R.layout.empty_state
            }
            onBind { viewBinder, _ ->
                val animation = viewBinder.findViewById<LottieAnimationView>(R.id.animation_view)
                animation.setAnimation(R.raw.empty_list)
                animation.playAnimation()
            }
            onUnbind { viewBinder, _ ->
                val animation = viewBinder.findViewById<LottieAnimationView>(R.id.animation_view)
                animation.pauseAnimation()
            }
        }
    }

    private inner class PagingModuleImpl : PagingModule() {
        init {
            config {
                layoutResource = R.layout.load_more
                visibleThreshold = 3
            }
            onLoadMore {
                viewModel.onLoadMore()
            }
        }
    }

    private inner class ItemSelectionModuleImpl : ItemSelectionModule() {
        init {
            config {
                selectionType = SelectionType.Multiple
            }
            onStartSelection {
                setToolbarColor(ColorDrawable(ContextCompat.getColor(this@CompleteExampleActivity, R.color.dark_gray)))
                toolbarMenu?.findItem(R.id.action_start_selection)?.isVisible = false
            }
            onUpdateSelection { selectedCount ->
                if (oneAdapter.modules.itemSelectionModule?.actions?.isSelectionActive() == true) {
                    setToolbarText("$selectedCount selected")
                    toolbarMenu?.findItem(R.id.action_delete)?.isVisible = true
                }
            }
            onEndSelection {
                setToolbarText(getString(R.string.app_name))
                toolbarMenu?.findItem(R.id.action_delete)?.isVisible = false
                toolbarMenu?.findItem(R.id.action_start_selection)?.isVisible = true
                setToolbarColor(ColorDrawable(ContextCompat.getColor(this@CompleteExampleActivity, R.color.colorPrimary)))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        toolbarMenu = menu
        menuInflater.inflate(R.menu.menu_main, toolbarMenu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                oneAdapter.modules.itemSelectionModule?.actions?.removeSelectedItems()
                return true
            }
            R.id.action_start_selection -> {
                oneAdapter.modules.itemSelectionModule?.actions?.startSelection()
                return true
            }
	        R.id.action_select_all -> {
		        oneAdapter.modules.itemSelectionModule?.actions?.selectAll()
		        return true
	        }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun paintSwipeRight(canvas: Canvas, xAxisOffset: Float, rootView: View) {
        val icon = ContextCompat.getDrawable(this@CompleteExampleActivity, R.drawable.ic_snooze_white_24dp)
        val colorDrawable = ColorDrawable(Color.DKGRAY)

        icon?.let {
            val middle = rootView.bottom - rootView.top
            var top = rootView.top
            var bottom = rootView.bottom
            var right = rootView.left + xAxisOffset.toInt()
            var left = rootView.left
            colorDrawable.setBounds(left, top, right, bottom)
            colorDrawable.draw(canvas)

            top = rootView.top + (middle / 2) - (it.intrinsicHeight / 2)
            bottom = top + it.intrinsicHeight
            left = rootView.left + ICON_MARGIN
            right = left + it.intrinsicWidth
            it.setBounds(left, top, right, bottom)
            it.draw(canvas)
        }
    }

    private fun paintSwipeLeft(canvas: Canvas, xAxisOffset: Float, rootView: View) {
        val icon = ContextCompat.getDrawable(this@CompleteExampleActivity, R.drawable.ic_delete_white_24dp)
        val colorDrawable = ColorDrawable(Color.RED)

        icon?.let {
            val middle = rootView.bottom - rootView.top
            var top = rootView.top
            var bottom = rootView.bottom
            var right = rootView.right
            var left = rootView.right + xAxisOffset.toInt()
            colorDrawable.setBounds(left, top, right, bottom)
            colorDrawable.draw(canvas)

            top = rootView.top + (middle / 2) - (it.intrinsicHeight / 2)
            bottom = top + it.intrinsicHeight
            right = rootView.right - ICON_MARGIN
            left = right - it.intrinsicWidth
            it.setBounds(left, top, right, bottom)
            it.draw(canvas)
        }
    }
}