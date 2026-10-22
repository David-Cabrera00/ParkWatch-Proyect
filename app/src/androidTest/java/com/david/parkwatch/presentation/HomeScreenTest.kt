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
import com.david.parkwatch.presentation.home.HomeScreen
import com.david.parkwatch.presentation.theme.ParkWatchTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun emptyState_invokesSaveAction() {
        var saveClicked = false

        composeRule.setContent {
            ParkWatchTheme {
                HomeScreen(
                    location = null,
                    onSaveVehicleClick = { saveClicked = true },
                )
            }
        }

        composeRule.onNodeWithText(context.getString(R.string.home_empty_title))
            .assertIsDisplayed()
        scrollToAndClick(context.getString(R.string.home_save_vehicle))

        assertTrue(saveClicked)
    }

    @Test
    fun savedState_showsLocationAndInvokesViewAction() {
        var viewClicked = false
        val location = parkingLocation()

        composeRule.setContent {
            ParkWatchTheme {
                HomeScreen(
                    location = location,
                    onSaveVehicleClick = {},
                    onViewVehicleClick = { viewClicked = true },
                )
            }
        }

        composeRule.onNodeWithText("P2").assertIsDisplayed()
        composeRule.onNodeWithText("B").assertIsDisplayed()
        composeRule.onNodeWithText("14").assertIsDisplayed()
        scrollToAndClick(context.getString(R.string.home_view_vehicle))

        assertTrue(viewClicked)
    }

    private fun scrollToAndClick(label: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(label))
        composeRule.onNodeWithText(label).performClick()
    }

    private fun parkingLocation() = ParkingLocation(
        id = "home-test-id",
        floor = "P2",
        zone = "B",
        row = "14",
        spot = "27",
        parkedAt = System.currentTimeMillis() - 5 * 60_000L,
        createdAt = System.currentTimeMillis() - 5 * 60_000L,
        updatedAt = System.currentTimeMillis() - 5 * 60_000L,
    )
}
