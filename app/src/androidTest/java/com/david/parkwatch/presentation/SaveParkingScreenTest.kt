package com.david.parkwatch.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.david.parkwatch.R
import com.david.parkwatch.domain.model.ParkingLocation
import com.david.parkwatch.domain.model.VehicleType
import com.david.parkwatch.presentation.save.SaveParkingScreen
import com.david.parkwatch.presentation.theme.ParkWatchTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveParkingScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun editFlow_reviewsAndSavesUpdatedLocation() {
        var savedLocation: ParkingLocation? = null

        composeRule.setContent {
            ParkWatchTheme {
                SaveParkingScreen(
                    initialLocation = existingLocation(),
                    onSaveSuccess = { savedLocation = it },
                )
            }
        }

        repeat(4) {
            clickContinue()
        }

        val reviewTitle = context.getString(R.string.save_parking_review_title)
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(reviewTitle))
        composeRule.onNodeWithText(reviewTitle).assertIsDisplayed()
        scrollToAndClick(context.getString(R.string.save_parking_save))

        val successTitle = context.getString(R.string.save_parking_success_title)
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(successTitle))
        composeRule.onNodeWithText(successTitle).assertIsDisplayed()

        assertNotNull(savedLocation)
        assertEquals("P2", savedLocation?.floor)
        assertEquals("B", savedLocation?.zone)
        assertEquals("14", savedLocation?.row)
        assertEquals("27", savedLocation?.spot)
        assertEquals(VehicleType.CAR, savedLocation?.vehicleType)
        assertEquals("edit-test-id", savedLocation?.id)
    }

    private fun clickContinue() {
        scrollToAndClick(context.getString(R.string.save_parking_continue))
    }

    private fun scrollToAndClick(label: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(label))
        composeRule.onNodeWithText(label).performClick()
    }

    private fun existingLocation() = ParkingLocation(
        id = "edit-test-id",
        floor = "P2",
        zone = "B",
        row = "14",
        spot = "27",
        parkedAt = 1_700_000_000_000L,
        createdAt = 1_700_000_000_000L,
        updatedAt = 1_700_000_000_000L,
    )
}
