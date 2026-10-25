# 🚗 ParkWatch — Wear OS Smartwatch App

[![Wear OS](https://img.shields.io/badge/Wear%20OS-3.0%2B%20(API%2030%2B)-teal?logo=android&logoColor=white)](https://developer.android.com/wear)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose for Wear OS](https://img.shields.io/badge/Compose%20for%20Wear%20OS-Material%203-blue?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-30-informational)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/targetSdk-37-informational)](https://developer.android.com)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#)

**ParkWatch** es una aplicación *standalone* (independiente) para relojes inteligentes **Wear OS** diseñada para registrar, ubicar y gestionar el estacionamiento de tu vehículo (Automóvil, Motocicleta, Bicicleta o Scooter) en cuestión de segundos, sin fricción y **100% offline**.

---

## ✨ Características Principales

### 🧭 1. Brújula y Guía Offline (Radar)
* **Orientación en tiempo real:** Sensor de rotación vectorial y magnetómetro para guiarte directamente hacia las coordenadas de tu vehículo sin requerir conexión a Internet.
* **Pantalla de Llegada Dedicada:** Al acercarte a menos de 10 metros, la interfaz cambia fluidamente a una vista de llegada con botón rápido de **"Liberar lugar"** que archiva el estacionamiento en tu historial y cancela alarmas activas.

### ⚡ 2. Registro Ultrarrápido de Ubicación
* **1-Tap GPS:** Guarda tu posición y hora exacta con un solo toque desde la pantalla principal.
* **Dictado por Voz:** Reconocimiento de voz integrado (*"Piso 2, Zona B, Puesto 14"*) con motor de extracción de entidades (`VoiceParkingParser`).
* **Asistente Visual con Auto-avance:** Selector ergonómico de Piso, Zona, Fila y Puesto con confirmación táctil (háptica) inmediata y atajo *"Listo / Guardar ya"*.

### ✏️ 3. Edición Rápida (Quick Edit)
* Modifica campos específicos (Piso, Zona, Fila, Puesto o Tipo de vehículo) en 1 toque desde la vista de resumen sin reiniciar el asistente de guardado y preservando el temporizador activo del parquímetro.

### ⏱️ 4. Temporizador de Parquímetro Inteligente
* **Selector en Cuadrícula Compacta (2x2):** Botones directos de 15 min, 30 min, 45 min, 1 hora, 1.5 horas, 2 horas, 3 horas y 4 horas sin scroll vertical.
* **Notificaciones Proactivas:** Alerta vibratoria 15 minutos antes de expirar y al vencer el límite de tiempo.

### 📜 5. Historial Persistente
* Consulta tus estacionamientos anteriores con detalles de fecha, hora, duración de estadía y tipo de vehículo.

### ⌚ 6. Integración Nativa con Wear OS
* **Tile (Mosaico de Vista Rápida):** Acceso instantáneo al vehículo estacionado y tiempo transcurrido desde la carátula deslizando hacia los lados.
* **Complicación de Esfera:** Agrega el estado de estacionamiento en cualquier esfera de reloj compatible.
* **Corona Rotatoria Física (Rotary Scroll):** Desplazamiento natural y respuesta háptica en listas mediante la corona giratoria del smartwatch.

---

## 🏛️ Arquitectura del Proyecto

El proyecto sigue los principios de **Clean Architecture** estructurado en un único módulo (`:app`):

```
app/src/main/java/com/david/parkwatch/
├── data/
│   ├── location/         # LocationHelper (coordenadas GPS, cálculo de distancia y rumbo)
│   ├── preferences/      # AppPreferencesStore (tema, idioma, ajustes)
│   ├── repository/       # ParkingRepository (persistencia local JSON)
│   ├── sensor/           # CompassSensorManager (fusión de sensores para brújula)
│   └── timer/            # ParkingTimerManager & AlarmReceiver (notificaciones de parquímetro)
├── domain/
│   ├── model/            # Modelos de datos: ParkingLocation, VehicleType
│   └── parser/           # VoiceParkingParser (análisis semántico de voz)
└── presentation/
    ├── MainActivity.kt   # Punto de entrada y WearApp composable
    ├── navigation/       # ParkWatchNavHost y rutas de navegación Wear OS
    ├── component/        # Componentes UI reutilizables (Botones, Tarjetas, Selector de vehículo, Rotary)
    ├── home/             # HomeScreen (estados Vacío y Estacionado)
    ├── detail/           # ParkingDetailScreen (resumen, acciones de mapa y compartir)
    ├── compass/          # CompassScreen (interfaz de brújula analógica y llegada)
    ├── save/             # SaveParkingScreen (asistente de guardado y edición rápida)
    ├── timer/            # SetTimerScreen (cuadrícula de parquímetro)
    ├── history/          # HistoryScreen (historial de parqueos)
    ├── settings/         # SettingsScreen (tema e idioma)
    ├── tile/             # ParkWatchTileService (Tile de Wear OS)
    ├── complication/     # ParkWatchComplicationService (Complicaciones)
    └── theme/            # Tokens de diseño oscuro, paleta esmeralda (#2EBD6E) y espaciados
```

---

## 🛠️ Tecnologías y Dependencias

* **Lenguaje:** Kotlin 2.2.10
* **Build Tool:** Gradle 9.6 / Android Gradle Plugin 9.4.1
* **UI Toolkit:** Jetpack Compose para Wear OS (`androidx.wear.compose.material3:1.5.6`)
* **Navegación:** `androidx.wear.compose:compose-navigation:1.5.6`
* **Tiles & Protolayout:** `androidx.wear.tiles:1.4.1` / `androidx.wear.protolayout:1.2.1`
* **Complicaciones:** `androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1`
* **Pruebas:** JUnit 4, Robolectric 4.17

---

## 🚀 Compilación y Ejecución

### Requisitos previos
* **JDK:** Java 21 (`jdk-21.0.10` recomendado)
* **Android SDK:** Platform API 37 (Build Tools 35+)

### Comandos principales

```bash
# Compilar y ejecutar pruebas unitarias
./gradlew testDebugUnitTest

# Generar APK de depuración
./gradlew assembleDebug

# Generar APK de producción (con optimización R8 / ProGuard)
./gradlew assembleRelease

# Generar App Bundle para Google Play Store
./gradlew bundleRelease

# Verificación de Lint
./gradlew lint
```

Los artefactos generados se ubican en:
* **APK Debug:** `app/build/outputs/apk/debug/app-debug.apk`
* **APK Release:** `app/build/outputs/apk/release/app-release-unsigned.apk`
* **AAB Release:** `app/build/outputs/bundle/release/app-release.aab`

---

## 📄 Licencia

Desarrollado con dedicación para la plataforma Wear OS. Todos los derechos reservados.

