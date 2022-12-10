@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.remove

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotContain
import org.junit.Test
import org.junit.runner.RunWith

private const val NUMBER_OF_MODELS = 4

@RunWith(AndroidJUnit4::class)
class WhenCallingRemoveOnMultipleItemsWithModels_ThenInternalState_ShouldBeUpdated : BaseTest() {

    @Test
    fun test() {
        configure {
            val models = modelGenerator.generateModels(NUMBER_OF_MODELS)
            val modelsToRemove = models.subList(1, 3)
            val oldItemCount = NUMBER_OF_MODELS

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(modulesGenerator.generateValidItemModule())
                    setItems(models.toMutableList())
                }
            }
            actOnActivity {
                oneAdapter.remove(modelsToRemove)
            }
            untilAsserted {
                val newItemList = oneAdapter.internalAdapter.data
                val newItemCount = oneAdapter.itemCount
                newItemList shouldNotContain modelsToRemove
                newItemCount shouldEqualTo (oldItemCount - modelsToRemove.size)
            }
        }
    }
}