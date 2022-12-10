@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.modules.item

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel1
import com.idanatz.oneadapter.models.TestModel2
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenAddingManyDifferentItems_ThenOnBind_ShouldBeCalledOnceForEachItemOnScreen : BaseTest() {

    private val testedLayoutResource = R.layout.test_model_small

    @Test
    fun test() {
        configure {
            val numberOfHoldersInScreen = getNumberOfHoldersThatCanBeOnScreen(testedLayoutResource)
            val models = modelGenerator.generateDifferentModels(numberOfHoldersInScreen) // about 70 items

            prepareOnActivity {
                oneAdapter.attachItemModules(TestItemModule1(), TestItemModule2())
            }
            actOnActivity {
                oneAdapter.setItems(models)
            }
            untilAsserted {
                models.sumBy { it.onBindCalls } shouldEqualTo numberOfHoldersInScreen
            }
        }
    }

    inner class TestItemModule1 : ItemModule<TestModel1>() {
        init {
            config = modulesGenerator.generateValidItemModuleConfig(testedLayoutResource)
            onBind { model, _, _ ->
                model.onBindCalls++
            }
        }
    }

    inner class TestItemModule2 : ItemModule<TestModel2>() {
        init {
            config = modulesGenerator.generateValidItemModuleConfig(testedLayoutResource)
            onBind { model, _, _ ->
                model.onBindCalls++
            }
        }
    }
}