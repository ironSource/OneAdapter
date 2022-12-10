@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.update

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

private const val NUMBER_OF_MODELS = 1
private const val UPDATED_VALUE = "updated"

@RunWith(AndroidJUnit4::class)
class WhenCallingUpdateOnSingleItemWithIndex_ThenOnBind_ShouldBeCalledWithUpdatedValues : BaseTest() {

    @Test
    fun test() {
        configure {
            val modelToUpdate = modelGenerator.generateModel()
            val oldItemCount = NUMBER_OF_MODELS

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(TestItemModule())
                    setItems(mutableListOf(modelToUpdate))
                }
            }
            actOnActivity {
                modelToUpdate.content = UPDATED_VALUE
                oneAdapter.update(0)
            }
            untilAsserted {
                val newItemCount = oneAdapter.itemCount
                val model = (oneAdapter.internalAdapter.data[0] as TestModel)

                model.content shouldBeEqualTo UPDATED_VALUE
                model.onBindCalls shouldEqualTo 2 // 1 for the prepare and 1 for the update
                newItemCount shouldEqualTo oldItemCount
            }
        }
    }

    inner class TestItemModule : ItemModule<TestModel>() {
        init {
        	config = modulesGenerator.generateValidItemModuleConfig(R.layout.test_model_large)
            onBind { model, _, _ ->
                model.onBindCalls++
            }
        }
    }
}