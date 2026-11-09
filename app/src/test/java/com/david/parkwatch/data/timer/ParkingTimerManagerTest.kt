package com.david.parkwatch.data.timer

import android.app.AlarmManager
import android.content.Context
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ParkingTimerManagerTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Test
    fun scheduleTimer_schedulesAlarm_whenExpiresInFuture() {
        val futureExpiresAt = System.currentTimeMillis() + 30 * 60 * 1000L
        ParkingTimerManager.scheduleTimer(context, futureExpiresAt)

        val shadowAlarmManager = shadowOf(alarmManager)
        assertNotNull(shadowAlarmManager.peekNextScheduledAlarm())
    }

    @Test
    fun scheduleTimer_doesNotSchedule_whenExpiresInPast() {
        val pastExpiresAt = System.currentTimeMillis() - 1000L
        ParkingTimerManager.scheduleTimer(context, pastExpiresAt)

        val shadowAlarmManager = shadowOf(alarmManager)
        assertNull(shadowAlarmManager.peekNextScheduledAlarm())
    }

    @Test
    fun cancelTimer_cancelsScheduledAlarms() {
        val futureExpiresAt = System.currentTimeMillis() + 45 * 60 * 1000L
        ParkingTimerManager.scheduleTimer(context, futureExpiresAt)
        val shadowAlarmManager = shadowOf(alarmManager)
        assertNotNull(shadowAlarmManager.peekNextScheduledAlarm())

        ParkingTimerManager.cancelTimer(context)
        assertNull(shadowAlarmManager.peekNextScheduledAlarm())
    }
}
