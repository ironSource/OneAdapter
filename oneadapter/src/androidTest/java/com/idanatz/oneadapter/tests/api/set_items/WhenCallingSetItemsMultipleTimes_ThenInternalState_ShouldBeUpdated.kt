@file:Suppress("ClassName")

package com.idanatz.oneadapter.tests.api.set_items

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.idanatz.oneadapter.helpers.BaseTest
import com.idanatz.oneadapter.models.TestModel
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

private const val NUM_OF_RUNS = 10
private const val DELAY_BETWEEN_RUNS = 250L

@RunWith(AndroidJUnit4::class)
class WhenCallingSetItemsMultipleTimes_ThenInternalState_ShouldBeUpdated : BaseTest() {

    @Test
    fun test() {
        configure {
            val modelsListToSet = mutableListOf<List<TestModel>>()
			for (i in 0 until NUM_OF_RUNS) {
				modelsListToSet.add(modelGenerator.generateModels(Random.nextInt(1, 15)))
			}
            var oldItemCount = -1

            prepareOnActivity {
                oneAdapter.run {
                    attachItemModule(modulesGenerator.generateValidItemModule())
                    oldItemCount = itemCount
                }
            }
            actOnActivity {
				for (i in 0 until NUM_OF_RUNS) {
					runWithDelay((i + 1) * DELAY_BETWEEN_RUNS) {
						oneAdapter.setItems(modelsListToSet[i])
					}
				}
            }
            untilAsserted(assertTimeout = NUM_OF_RUNS * DELAY_BETWEEN_RUNS + 500) {
                val newItemList = oneAdapter.internalAdapter.data
                val newItemCount = oneAdapter.itemCount
                newItemList shouldContainAll modelsListToSet.last()
                newItemCount shouldEqualTo (oldItemCount + modelsListToSet.last().size)
            }
        }
    }
}