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

private const val NUMBER_OF_MODELS = 2
private const val UPDATED_VALUE_1 = "updated1"
private const val UPDATED_VALUE_2 = "updated2"

@RunWith(AndroidJUnit4::class)
class WhenCallingUpdateMultipleItemsWithModel_ThenOnBind_ShouldBeCalledWithUpdatedValues : BaseTest() {

    @Test
    fun test() {
        configure {
            val modelsToUpdate = modelGenerator.generateModels(NUMBER_OF_MODELS)
            val oldItemCount = NUMBER_OF_MODELS

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(TestItemModule())
                    setItems(modelsToUpdate.toMutableList())
                }
            }
            actOnActivity {
                modelsToUpdate[0].content = UPDATED_VALUE_1
                modelsToUpdate[1].content = UPDATED_VALUE_2
                oneAdapter.update(modelsToUpdate)
            }
            untilAsserted {
                val newItemCount = oneAdapter.itemCount
                val model1 = (oneAdapter.internalAdapter.data[0] as TestModel)
                val model2 = (oneAdapter.internalAdapter.data[1] as TestModel)

                model1.content shouldBeEqualTo UPDATED_VALUE_1
                model1.onBindCalls shouldEqualTo 2 // 1 for the prepare and 1 for the update
                model2.content shouldBeEqualTo UPDATED_VALUE_2
                model2.onBindCalls shouldEqualTo 2 // 1 for the prepare and 1 for the update
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