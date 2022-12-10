@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.view_type

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.holders.EmptyIndicator
import com.idanatz.oneadapter.external.holders.LoadingIndicator
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenCallingGetItemViewTypeFromClass_ThenItemViewTypes_ShouldBeCorrect : BaseTest() {

    @Test
    fun test() {
        configure {
            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(modulesGenerator.generateValidItemModule())
                    attachPagingModule(modulesGenerator.generateValidPagingModule())
                    attachEmptinessModule(modulesGenerator.generateValidEmptinessModule())
                }
            }
            actOnActivity {
                oneAdapter.add(modelGenerator.generateModel())
            }
            untilAsserted {
                oneAdapter.getItemViewTypeFromClass(TestModel::class.java) shouldEqualTo 0
                oneAdapter.getItemViewTypeFromClass(LoadingIndicator::class.java) shouldEqualTo 1
                oneAdapter.getItemViewTypeFromClass(EmptyIndicator::class.java) shouldEqualTo 2
            }
        }
    }
}