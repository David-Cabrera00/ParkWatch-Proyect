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
import com.david.parkwatch.presentation.detail.ParkingDetailScreen
import com.david.parkwatch.presentation.theme.ParkWatchTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParkingDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun delete_requiresExplicitConfirmation() {
        var deleted = false
        setDetailContent(onDeleteConfirmed = { deleted = true })

        scrollToAndClick(context.getString(R.string.detail_delete))

        val confirmTitle = context.getString(R.string.delete_confirm_title)
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(confirmTitle))
        composeRule.onNodeWithText(confirmTitle).assertIsDisplayed()
        assertFalse(deleted)

        scrollToAndClick(context.getString(R.string.delete_confirm_delete))

        assertTrue(deleted)
    }

    @Test
    fun delete_cancelReturnsToDetailWithoutDeleting() {
        var deleted = false
        setDetailContent(onDeleteConfirmed = { deleted = true })

        scrollToAndClick(context.getString(R.string.detail_delete))
        scrollToAndClick(context.getString(R.string.delete_confirm_cancel))

        composeRule.onNode(hasScrollAction())
            .performScrollToNode(hasText(context.getString(R.string.detail_edit)))
        composeRule.onNodeWithText(context.getString(R.string.detail_edit))
            .assertIsDisplayed()
        assertFalse(deleted)
    }

    @Test
    fun edit_invokesEditAction() {
        var editClicked = false
        setDetailContent(onEditClick = { editClicked = true })

        scrollToAndClick(context.getString(R.string.detail_edit))

        assertTrue(editClicked)
    }

    @Test
    fun compass_displayedAndClickableWhenCoordinatesPresent() {
        var compassClicked = false
        composeRule.setContent {
            ParkWatchTheme {
                ParkingDetailScreen(
                    location = parkingLocation().copy(latitude = 40.416775, longitude = -3.703790),
                    onEditClick = {},
                    onCompassClick = { compassClicked = true },
                    onDeleteConfirmed = {},
                    onNavigateBack = {},
                )
            }
        }

        val compassLabel = context.getString(R.string.compass_guide_me)
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(compassLabel))
        composeRule.onNodeWithText(compassLabel).assertIsDisplayed().performClick()

        assertTrue(compassClicked)
    }

    private fun scrollToAndClick(label: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(label))
        composeRule.onNodeWithText(label).performClick()
    }

    private fun setDetailContent(
        onEditClick: () -> Unit = {},
        onDeleteConfirmed: () -> Unit = {},
    ) {
        composeRule.setContent {
            ParkWatchTheme {
                ParkingDetailScreen(
                    location = parkingLocation(),
                    onEditClick = onEditClick,
                    onDeleteConfirmed = onDeleteConfirmed,
                    onNavigateBack = {},
                )
            }
        }
    }

    private fun parkingLocation() = ParkingLocation(
        id = "detail-test-id",
        floor = "P2",
        zone = "B",
        row = "14",
        spot = "27",
        parkedAt = System.currentTimeMillis() - 5 * 60_000L,
        createdAt = System.currentTimeMillis() - 5 * 60_000L,
        updatedAt = System.currentTimeMillis() - 5 * 60_000L,
    )
}
