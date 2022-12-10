@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.visible_items

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenAddingFewItems_ThenCallingVisibleItems_ShouldReturnCorrectResult : BaseTest() {

    private val testedLayoutResource = R.layout.test_model_large

    @Test
    fun test() {
        configure {
            val numberOfHoldersInScreen = getNumberOfHoldersThatCanBeOnScreen(testedLayoutResource)
            val models = modelGenerator.generateModels(numberOfHoldersInScreen + 1) // + 1 item out of the screen

            prepareOnActivity {
                oneAdapter.attachItemModule(modulesGenerator.generateValidItemModule(testedLayoutResource))
            }
            actOnActivity {
                oneAdapter.setItems(models)
            }
            untilAsserted {
				oneAdapter.getVisibleItemIndexes().size shouldEqualTo numberOfHoldersInScreen
            }
        }
    }
}