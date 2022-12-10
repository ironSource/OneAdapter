package com.idanatz.oneadapter.dsl

import android.os.Handler
import androidx.test.core.app.ActivityScenario
import com.idanatz.oneadapter.helpers.TestActivity
import org.amshove.kluent.should
import org.amshove.kluent.shouldEqualTo
import org.awaitility.Awaitility
import java.util.concurrent.TimeUnit

class TestBuilder(
		private val activityScenario: ActivityScenario<TestActivity>,
		private val handler: Handler,
		private var delayFlag: Boolean = false
) {

	fun prepare(block: () -> Unit) = block()
	fun prepareOnActivity(block: (activity: TestActivity) -> Unit) {
		activityScenario.onActivity(block)
	}

	fun act(block: () -> Unit) = block()
	fun actOnActivity(block: (activity: TestActivity) -> Unit) {
		activityScenario.onActivity(block)
	}

	fun assert(block: () -> Unit) = block()
	fun untilAsserted(assertTimeout: Long = 5000L, assertDelay: Long = 0L, block: () -> Unit) {
		handleDelay(assertDelay)

		Awaitility.setDefaultTimeout(assertTimeout, TimeUnit.MILLISECONDS)
		Awaitility.await().untilAsserted {
			delayFlag shouldEqualTo false
			block()
		}
	}

	private fun handleDelay(assertDelay: Long) {
		if (assertDelay != 0L) {
			delayFlag = true
			handler.postDelayed({
				delayFlag = false
			}, assertDelay)
		}
	}
}