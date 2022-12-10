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
class WhenAddingFewItems_ThenOnBind_ShouldBeCalledOnceForEachItemOnScreen : BaseTest() {

    private val testedLayoutResource = R.layout.test_model_large

    @Test
    fun test() {
        configure {
            val numberOfHoldersInScreen = getNumberOfHoldersThatCanBeOnScreen(testedLayoutResource)
            val models = modelGenerator.generateModels(numberOfHoldersInScreen) // about 6 items

            prepareOnActivity {
                oneAdapter.attachItemModule(TestItemModule())
            }
            actOnActivity {
                oneAdapter.setItems(models)
            }
            untilAsserted {
                models.sumBy { it.onBindCalls } shouldEqualTo numberOfHoldersInScreen
            }
        }
    }

    inner class TestItemModule : ItemModule<TestModel>() {
        init {
            config = modulesGenerator.generateValidItemModuleConfig(testedLayoutResource)
            onBind { model, _, _ ->
                model.onBindCalls++
            }
        }
    }
}