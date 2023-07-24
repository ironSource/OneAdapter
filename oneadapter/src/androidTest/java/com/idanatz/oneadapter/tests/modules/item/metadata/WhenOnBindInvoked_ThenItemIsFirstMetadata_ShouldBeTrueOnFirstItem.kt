@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.modules.item.metadata

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.ItemModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel
import com.idanatz.oneadapter.models.TestModel1
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenOnBindInvoked_ThenItemIsFirstMetadata_ShouldBeTrueOnFirstItem : BaseTest() {

	private var firstCondition = booleanArrayOf(false, false, false, false, false)

	@Test
	fun test() {
		configure {
			prepareOnActivity {
				oneAdapter.run {
					attachItemModule(TestItemModule())
					setItems(modelGenerator.generateModels(5).toMutableList())
				}
			}
			untilAsserted {
				firstCondition shouldContainSame booleanArrayOf(true, false, false, false, false)
			}
		}
	}

	inner class TestItemModule : ItemModule<TestModel>() {
		init {
			config = modulesGenerator.generateValidItemModuleConfig(R.layout.test_model_small)
			onBind { model, _, metadata ->
				firstCondition[metadata.position] = metadata.isFirst
			}
		}
	}
}