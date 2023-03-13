@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.position

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WhenPassingMissingItem_ThenGetItemPosition_ShouldReturnProperValue : BaseTest() {

	@Test
    fun test() {
        configure {
	        var position = 0
            act {
	            position = oneAdapter.getItemPosition(modelGenerator.generateModel())
            }
	        assert {
		        position shouldEqualTo -1
	        }
        }
    }
}