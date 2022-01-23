@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.ItemSelectionModule
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenCallingAttachItemsSelectionModule_ThenInternalState_ShouldBeUpdated : BaseTest() {

    @Test
    fun test() {
        configure {
            val testItemSelectionModule = ItemSelectionModule()

            prepareOnActivity {
                oneAdapter.run {
                    attachItemSelectionModule(testItemSelectionModule)
                }
            }
            untilAsserted {
                oneAdapter.modules.itemSelectionModule shouldEqual testItemSelectionModule
                oneAdapter.modules.itemSelectionModule?.actions shouldNotBe null
            }
        }
    }
}