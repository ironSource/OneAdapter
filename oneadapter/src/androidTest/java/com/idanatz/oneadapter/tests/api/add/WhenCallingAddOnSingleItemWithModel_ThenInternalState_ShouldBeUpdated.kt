@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.add

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenCallingAddOnSingleItemWithModel_ThenInternalState_ShouldBeUpdated : BaseTest() {

    @Test
    fun test() {
        configure {
            val modelToAdd = modelGenerator.generateModel()
            var oldItemCount = -1

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(modulesGenerator.generateValidItemModule())
                    oldItemCount = itemCount
                }
            }
            actOnActivity {
                oneAdapter.add(modelToAdd)
            }
            untilAsserted {
                val newItemList = oneAdapter.internalAdapter.data
                val newItemCount = oneAdapter.itemCount
                newItemList shouldContain modelToAdd
                newItemCount shouldEqualTo (oldItemCount + 1)
            }
        }
    }
}