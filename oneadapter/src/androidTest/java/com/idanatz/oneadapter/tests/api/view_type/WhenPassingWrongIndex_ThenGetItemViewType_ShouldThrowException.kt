@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.view_type

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.external.holders.EmptyIndicator
import com.idanatz.oneadapter.helpers.BaseTest
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IndexOutOfBoundsException

@RunWith(AndroidJUnit4::class)
class WhenPassingWrongIndex_ThenGetItemViewType_ShouldThrowException : BaseTest() {

    @Test(expected = IndexOutOfBoundsException::class)
    fun test() {
        configure {
            act {
                oneAdapter.getItemViewType(0)
            }
        }
    }
}