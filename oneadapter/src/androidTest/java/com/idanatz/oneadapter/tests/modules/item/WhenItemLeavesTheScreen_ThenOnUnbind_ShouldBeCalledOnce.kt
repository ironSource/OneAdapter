@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.modules.item

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenItemLeavesTheScreen_ThenOnUnbind_ShouldBeCalledOnce : BaseTest() {

    private val testedLayoutResource = R.layout.test_model_large

    @Test
    fun test() {
        configure {
            val numberOfHoldersInScreen = getNumberOfHoldersThatCanBeOnScreen(testedLayoutResource)
            val models = modelGenerator.generateModels(numberOfHoldersInScreen + 4) // create enough items that some can get recycled

            prepareOnActivity {
                oneAdapter.attachItemModule(TestItemModule())
                oneAdapter.setItems(models)
            }
            actOnActivity {
                runWithDelay { // run with delay to let the items settle
                    recyclerView.smoothScrollToPosition(oneAdapter.itemCount - 1)
                }
            }
            untilAsserted {
                models.sumBy { it.onUnbindCalls } shouldEqualTo 1
            }
        }
    }

    inner class TestItemModule : ItemModule<TestModel>() {
        init {
            config = modulesGenerator.generateValidItemModuleConfig(testedLayoutResource)
            onUnbind { model, _, _ ->
                model.onUnbindCalls++
            }
        }
    }
}