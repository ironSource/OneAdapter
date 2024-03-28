package com.idanatz.sample.examples.features

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.bumptech.glide.Glide
import com.idanatz.oneadapter.OneAdapter
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.external.modules.ItemSelectionModule
import com.idanatz.oneadapter.external.modules.ItemSelectionModuleConfig.*
import com.idanatz.oneadapter.external.states.SelectionState
import com.idanatz.oneadapter.external.states.SelectionStateConfig
import com.idanatz.oneadapter.sample.R
import com.idanatz.sample.models.MessageModel
import com.idanatz.sample.examples.BaseExampleActivity

class ItemSelectionModuleActivity : BaseExampleActivity() {

    private var toolbarMenu: Menu? = null
    lateinit var oneAdapter: OneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oneAdapter = OneAdapter(recyclerView) {
            itemModules += MessageItem()
            itemSelectionModule = ItemSelectionModuleImpl()
        }

        oneAdapter.setItems(modelGenerator.generateMessages(10))
    }

    override fun onBackPressed() {
        if (oneAdapter.modules.itemSelectionModule?.actions?.isSelectionActive() == true) {
            oneAdapter.modules.itemSelectionModule?.actions?.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

    private inner class MessageItem : ItemModule<MessageModel>() {
        init {
            config {
                layoutResource = R.layout.message_model
            }
            onBind { model, viewBinder, metadata ->
                val title = viewBinder.findViewById<TextView>(R.id.title)
                val body = viewBinder.findViewById<TextView>(R.id.body)
                val avatarImage = viewBinder.findViewById<ImageView>(R.id.avatarImage)
                val selectedLayer = viewBinder.findViewById<ImageView>(R.id.selected_layer)

                title.text = model.title
                body.text = model.body
                Glide.with(viewBinder.rootView).load(model.avatarImageId).into(avatarImage)

                // selected UI
                avatarImage.alpha = if (metadata.isSelected) 0.5f else 1f
                selectedLayer.visibility = if (metadata.isSelected) View.VISIBLE else View.GONE
                viewBinder.rootView.setBackgroundColor(if (metadata.isSelected) ContextCompat.getColor(this@ItemSelectionModuleActivity, R.color.light_gray) else Color.WHITE)
            }
            states += SelectionState<MessageModel>().apply {
                config {
                    enabled = true
                    selectionTrigger = SelectionStateConfig.SelectionTrigger.LongClick
                }
                onSelected { model, selected ->
                    val message = "${model.title} " + if (selected) "selected" else "unselected"
                    Toast.makeText(this@ItemSelectionModuleActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class ItemSelectionModuleImpl : ItemSelectionModule() {
        init {
            config {
                selectionType = SelectionType.Multiple
            }
            onStartSelection {
                setToolbarColor(ColorDrawable(ContextCompat.getColor(this@ItemSelectionModuleActivity, R.color.dark_gray)))
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
                setToolbarColor(ColorDrawable(ContextCompat.getColor(this@ItemSelectionModuleActivity, R.color.colorPrimary)))
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
}