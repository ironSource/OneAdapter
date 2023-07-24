package com.idanatz.oneadapter.helpers

import android.os.Handler
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idanatz.oneadapter.generators.ModelGenerator
import org.junit.Before
import androidx.test.ext.junit.rules.activityScenarioRule
import com.idanatz.oneadapter.OneAdapter
import com.idanatz.oneadapter.dsl.TestBuilder
import com.idanatz.oneadapter.generators.ModulesGenerator
import com.idanatz.oneadapter.test.R
import org.awaitility.Awaitility
import org.junit.After
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

open class BaseTest {

    protected lateinit var oneAdapter: OneAdapter
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var modelGenerator: ModelGenerator
    protected lateinit var modulesGenerator: ModulesGenerator
    protected lateinit var touchSimulator: TouchSimulator

    private lateinit var handler: Handler
    private var screenHeight: Int = 0
    private var smallHolderHeight: Int = 0
    private var largeHolderHeight: Int = 0

    @get:Rule var activityScenarioRule = activityScenarioRule<TestActivity>()

    @Before
    open fun setup() {
        modelGenerator = ModelGenerator()
        modulesGenerator = ModulesGenerator()

        activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
        activityScenarioRule.scenario.onActivity {
            handler = Handler()
            touchSimulator = TouchSimulator(handler)

            screenHeight = it.resources.displayMetrics.heightPixels
            smallHolderHeight = it.resources.getDimension(R.dimen.small_model_height).toInt()
            largeHolderHeight = it.resources.getDimension(R.dimen.large_model_height).toInt()

            recyclerView = it.findViewById<RecyclerView>(R.id.recycler).apply {
                layoutManager = LinearLayoutManager(it)
                oneAdapter = OneAdapter(this)
            }
        }
    }

    @After
    open fun cleanup() {
        handler.removeCallbacksAndMessages(null)
        Awaitility.reset()
    }

    fun configure(block: TestBuilder.() -> Unit) {
        TestBuilder(activityScenarioRule.scenario, handler).run { block() }
    }

    protected fun runWithDelay(delayInMillis: Long = 600L, block: () -> Unit) {
        handler.postDelayed(block, delayInMillis)
    }

    protected fun catchException(block: () -> Unit): Throwable {
        val countDownLatch = CountDownLatch(1)
        var thrownException: Throwable? = null

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            thrownException = e
            countDownLatch.countDown()
        }

        block()
        countDownLatch.await(10L, TimeUnit.SECONDS)
        return thrownException ?: throw IllegalStateException("No exception was caught")
    }

    protected fun getNumberOfHoldersThatCanBeOnScreen(@LayoutRes layoutId: Int): Int {
        return when (layoutId) {
            R.layout.test_model_small -> screenHeight / smallHolderHeight
            R.layout.test_model_large -> screenHeight / largeHolderHeight
            else -> 0
        }
    }
}