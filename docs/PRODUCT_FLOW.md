# ParkWatch — Product Flow

## Main Goal

Allow the user to save and retrieve the location of their parked vehicle quickly and effortlessly from a Wear OS smartwatch.

---

## Main User Flow

### 1. No saved parking location (Empty State)

The Home screen presents a clean, circular-optimized empty view with multiple registration options:
1. **Primary Button**: "Guardar vehículo" → Launches the Step-by-Step Wizard (Floor → Zone → Row → Spot → Review).
2. **2-Column Quick Action Row**:
   - `[ 📍 GPS rápido ]`: 1-tap instant save using current GPS coordinates & timestamp.
   - `[ 🎙️ Por voz ]`: Direct voice dictation parsed by `VoiceParkingParser`.
3. **2-Column Utility Row**:
   - `[ Historial ]`: View previous parking sessions.
   - `[ Ajustes ]`: Configure theme (System, Light, Dark) and language (System, Spanish, English).

---

### 2. Saved parking location (Active State)

The Home screen displays an elevated card showing:
- Vehicle type icon & name (Car, Motorcycle, Bicycle, Electric Scooter).
- Floor, Zone, Row, and Spot.
- Elapsed parking time ("Hace 25 min").
- Parquímetro countdown badge (if active timer is set).

**Actions from Home:**
1. **Primary 1-Tap Action**:
   - If GPS coordinates are saved: **"🧭 Guiarme al vehículo"** (opens CompassScreen immediately).
   - If manual-only: **"Ver detalles"** (opens ParkingDetailScreen).
2. **Direct Card Tap**: Tapping `ParkLocationCard` directly opens `ParkingDetailScreen`.
3. **Secondary Action**: "Ver detalles" sits directly below the Compass button.

---

### 3. Offline Compass & Radar Guidance Flow

```
Home (Vehicle Saved with GPS)
→ Tap "🧭 Guiarme al vehículo"
→ CompassScreen
```

* **Live Compass Dial**:
  - Uses `CompassSensorManager` (Rotation Vector / Accelerometer + Magnetometer fusion).
  - Dynamic analog needle pointing directly to target coordinates.
  - Heading and distance readout ("A 45 m").
  - Tapping the center vehicle badge allows ending guidance anytime.

* **Arrival Screen (< 10 meters)**:
  - When the user reaches within 10 meters of their vehicle, the dial transitions smoothly to a dedicated **Arrival Screen**:
    - Centered green vehicle badge (52dp).
    - "¡Llegaste a tu [Vehículo]!"
    - Primary button: **"Liberar lugar"**.
  - Tapping "Liberar lugar" triggers a confirmation dialog:
    - *"¿Finalizar parqueo? Se guardará en tu historial"*.
    - Confirmed: Archiva la ubicación en el historial persistente, cancela alarmas de parquímetro activas, actualiza Tiles y Complicaciones, muestra Toast y regresa al Home.

---

### 4. Step-by-Step Save Wizard & Quick Shortcuts

1. **Step 1 - Floor**: P1, P2, P3, B1, B2 (Instant auto-advance with tactile haptic tick).
2. **Step 2 - Zone**: A, B, C, D (Instant auto-advance).
   - Shortcut available: *"Listo / Guardar ya"* to jump directly to Review.
3. **Step 3 - Row**: Options 1–15 or **"Sin fila"** (Instant auto-advance).
   - Shortcut available: *"Listo / Guardar ya"*.
4. **Step 4 - Spot**: Options 1–30 or "Sin puesto" (Instant auto-advance to Review).
5. **Step 5 - Review & Vehicle Selection**:
   - Preview card with vehicle badge.
   - Vehicle selector: Auto, Moto, Bici, Scooter.
   - Primary button: "Guardar ubicación".

---

### 5. 1-Tap Quick Edit Flow

```
Parking Detail → Tap "Editar ubicación" → SaveParkingScreen (Review Step)
```

* Opens directly in `SaveStep.REVIEW` (does not restart wizard from Floor 1).
* **Direct Field Chips (2x2 Grid)**:
  - `[ Piso: P1 ]` `[ Zona: A ]`
  - `[ Fila: 2 ]` `[ Puesto: 14 ]`
* Tapping any chip jumps directly to that specific picker. Once an option is chosen, it automatically returns to the Review screen.
* **Back Navigation**: Pressing back returns to Review without losing data, or pops back to Detail if already on Review.
* **Timer Preservation**: Editing location details preserves active `expiresAt` parking timers.

---

### 6. Parking Timer Flow (Parquímetro)

```
Parking Detail → Tap "Límite de tiempo" → SetTimerScreen
```

* **Compact 2-Column Preset Grid**:
  - `[ 15 min ]` `[ 30 min ]`
  - `[ 45 min ]` `[ 1 hora ]`
  - `[ 1 h 30 ]` `[ 2 horas ]`
  - `[ 3 horas ]` `[ 4 horas ]`
* **Zero-scroll UI**: Fits cleanly within 384x384 circular watch displays.
* **Proactive Notifications**:
  - Warning alert 15 minutes before expiration.
  - Final alarm when time expires.
* **Remove Timer**: Prominent button to cancel timer anytime.

---

### 7. Hardware Rotary Scroll & Haptics

* All vertical lists (`ScalingLazyColumn`) implement `.parkRotaryScroll(listState)`.
* Attaches physical focus (`.focusRequester().focusable()`) to capture rotating bezel and crown hardware inputs.
* Emits smooth inertia scrolling and tactile haptic ticks on snap intervals.

---

### 8. Native Wear OS Integration

* **Tile (ParkWatchTileService)**: Displays vehicle status, parking zone/spot, elapsed time, and 1-tap launcher from the watch face carousel.
* **Complication (ParkWatchComplicationService)**: Circular and short-text complications to display parking status directly on custom watch faces.
* **Share Location**: Uses Android system share sheet (`Intent.ACTION_SEND`) to share spot coordinates and Google Maps link via messaging apps or companion phone.

---

### 9. Core Offline Guarantee

ParkWatch is completely functional without internet connection.
GPS and voice recognition are optional enhancements that degrade gracefully to manual entry without blocking the user.
