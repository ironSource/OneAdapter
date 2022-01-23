@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.modules.paging

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.holders.LoadingIndicator
import com.idanatz.oneadapter.external.modules.PagingModule
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.test.R
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotContain
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenCallingSetItemsWithNoItems_ThenPaging_ShouldNotBeTriggered : BaseTest() {

	private var onBindCalls = 0
	private var onLoadMoreCalls = 0

	@Test
	fun test() {
		configure {
			prepareOnActivity {
				oneAdapter.run {
					attachItemModule(modulesGenerator.generateValidItemModule(R.layout.test_model_large))
					attachPagingModule(TestPagingModule())
				}
			}
			act {
				oneAdapter.setItems(listOf())
			}
			untilAsserted(assertDelay = 800) {
				onBindCalls shouldEqualTo 0
				onLoadMoreCalls shouldEqualTo 0
			}
		}
	}

	inner class TestPagingModule : PagingModule() {
		init {
			config = modulesGenerator.generateValidPagingModuleConfig()
			onBind { _, _ ->
				onBindCalls++
			}
			onLoadMore {
				onLoadMoreCalls++
			}
		}
	}
}