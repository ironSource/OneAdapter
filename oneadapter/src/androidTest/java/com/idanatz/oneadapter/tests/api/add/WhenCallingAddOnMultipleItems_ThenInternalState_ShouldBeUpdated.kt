@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.add

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

private const val NUM_OF_ITEMS_TO_ADD = 20

@RunWith(AndroidJUnit4::class)
class WhenCallingAddOnMultipleItems_ThenInternalState_ShouldBeUpdated : BaseTest() {

    @Test
    fun test() {
        configure {
            val modelsToAdd = modelGenerator.generateModels(NUM_OF_ITEMS_TO_ADD)
            var oldItemCount = -1

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(modulesGenerator.generateValidItemModule())
                    oldItemCount = itemCount
                }
            }
            actOnActivity {
                oneAdapter.add(modelsToAdd)
            }
            untilAsserted {
                val newItemList = oneAdapter.internalAdapter.data
                val newItemCount = oneAdapter.itemCount
                newItemList shouldContainAll modelsToAdd
                newItemCount shouldEqualTo (oldItemCount + NUM_OF_ITEMS_TO_ADD)
            }
        }
    }
}