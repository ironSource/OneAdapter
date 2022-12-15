@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.modules.paging

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.modules.PagingModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenCallingSetItemsWithLessThanThreshold_ThenOnLoadMore_ShouldBeCalledOnce_FixedSize : BaseTest() {

	private var onLoadMoreCalls = 0

	@Test
	fun test() {
		configure {
			prepareOnActivity {
				recyclerView.setHasFixedSize(true)
				oneAdapter.run {
					attachItemModule(modulesGenerator.generateValidItemModule(R.layout.test_model_large))
					attachPagingModule(TestPagingModule())
				}
			}
			act {
				oneAdapter.setItems(modelGenerator.generateModels(3).toMutableList())
			}
			untilAsserted(assertDelay = 500) {
				onLoadMoreCalls shouldEqualTo 1
			}
		}
	}

	inner class TestPagingModule : PagingModule() {
		init {
			config = modulesGenerator.generateValidPagingModuleConfig()
			onLoadMore {
				onLoadMoreCalls++
			}
		}
	}
}