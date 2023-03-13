@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.position

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

private const val POSITION_TO_SCROLL_TO = 10

@RunWith(AndroidJUnit4::class)
class WhenCallingGetItemPosition_ThenItemPosition_ShouldBeCorrect : BaseTest() {

    @Test
    fun test() {
        configure {
	        val models = modelGenerator.generateModels(15)
	        var positionToScrollTo = -1

            prepareOnActivity {
                oneAdapter.attachItemModule(TestItemModule())
            }
            actOnActivity {
	            oneAdapter.add(models)
	            runWithDelay { // run with delay to let the items settle
		            positionToScrollTo = oneAdapter.getItemPosition(models[POSITION_TO_SCROLL_TO])
		            recyclerView.smoothScrollToPosition(positionToScrollTo)
	            }
            }
            untilAsserted {
                oneAdapter.getItemPosition(models[0]) shouldEqualTo 0
	            positionToScrollTo shouldEqualTo POSITION_TO_SCROLL_TO
	            models[POSITION_TO_SCROLL_TO].onBindCalls shouldEqualTo 1
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