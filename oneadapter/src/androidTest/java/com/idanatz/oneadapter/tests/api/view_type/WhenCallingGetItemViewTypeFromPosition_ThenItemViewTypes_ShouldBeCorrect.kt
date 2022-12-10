@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.view_type

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.holders.EmptyIndicator
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel1
import com.idanatz.oneadapter.models.TestModel2
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenCallingGetItemViewTypeFromPosition_ThenItemViewTypes_ShouldBeCorrect : BaseTest() {

    @Test
    fun test() {
        configure {
            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(TestItemModule1())
                    attachItemModule(TestItemModule2())
                    attachEmptinessModule(modulesGenerator.generateValidEmptinessModule())
                }
            }
            actOnActivity {
                runWithDelay(250) { oneAdapter.add(modelGenerator.generateDifferentModels(2)) }
            }
            untilAsserted {
                oneAdapter.getItemViewType(0) shouldEqualTo 0
                oneAdapter.getItemViewType(1) shouldEqualTo 1
                oneAdapter.getItemViewTypeFromClass(EmptyIndicator::class.java) shouldEqualTo 2
            }
        }
    }

    inner class TestItemModule1 : ItemModule<TestModel1>() {
        init {
            config = modulesGenerator.generateValidItemModuleConfig(R.layout.test_model_small)
        }
    }

    inner class TestItemModule2 : ItemModule<TestModel2>() {
        init {
            config = modulesGenerator.generateValidItemModuleConfig(R.layout.test_model_small)
        }
    }
}