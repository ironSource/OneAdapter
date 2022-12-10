@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.add

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

private const val NUMBER_OF_MODELS = 5
private const val INDEX_TO_ADD = 3

@RunWith(AndroidJUnit4::class)
class AddSingleItemToIndex : BaseTest() {

    @Test
    fun test() {
        configure {
            val modelToAdd = modelGenerator.generateModel()
            val oldItemCount = NUMBER_OF_MODELS

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(modulesGenerator.generateValidItemModule())
					setItems(modelGenerator.generateModels(NUMBER_OF_MODELS).toMutableList())
                }
            }
            actOnActivity {
                oneAdapter.add(INDEX_TO_ADD, modelToAdd)
            }
            untilAsserted {
                val newItemList = oneAdapter.internalAdapter.data
                val newItemCount = oneAdapter.itemCount
                newItemList[INDEX_TO_ADD] shouldEqual modelToAdd
                newItemCount shouldEqualTo (oldItemCount + 1)
            }
        }
    }
}