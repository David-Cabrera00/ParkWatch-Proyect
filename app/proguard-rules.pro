# ParkWatch — ProGuard / R8 Rules

# Keep Wear OS Services, Receivers & Entry points
-keep class com.david.parkwatch.presentation.tile.ParkWatchTileService { *; }
-keep class com.david.parkwatch.presentation.complication.ParkWatchComplicationService { *; }
-keep class com.david.parkwatch.data.timer.ParkingTimerReceiver { *; }
-keep class com.david.parkwatch.data.timer.ParkingBootReceiver { *; }

# Domain models & Enums serialized to SharedPreferences/JSON
-keep class com.david.parkwatch.domain.model.** { *; }
-keepclassmembers enum com.david.parkwatch.domain.model.** { *; }
-keepclassmembers enum com.david.parkwatch.data.preferences.** { *; }
