# Kit de Lanzamiento y Publicación en Google Play Store — ParkWatch v1.0.0

Este documento contiene todos los recursos, textos oficiales y configuraciones requeridas para publicar **ParkWatch** en la **Google Play Console**.

---

## 1. Ficha de Play Store (Store Listing)

### Español (Predeterminado)

- **Nombre de la aplicación** (máx. 30 caracteres):
  `ParkWatch: Recordar Parqueo`

- **Descripción breve** (máx. 80 caracteres):
  `Encuentra tu vehículo en 1 toque. Brújula táctil y parquímetro en tu muñeca.`

- **Descripción completa** (máx. 4000 caracteres):
  ```text
  ParkWatch es la herramienta definitiva para Wear OS que te permite guardar y recuperar la ubicación de tu vehículo directamente desde tu muñeca, sin necesidad de sacar el teléfono.

  DISEÑADA EXCLUSIVAMENTE PARA WEAR OS
  • Registro en segundos: Selecciona piso, zona, fila y puesto con pastillas táctiles ergonómicas adaptadas a pantallas circulares.
  • Guardado Rápido por GPS: Registra tus coordenadas exactas con un solo toque.
  • Dictado por voz: Di en voz alta los datos de tu estacionamiento y la app los registrará al instante sin escribir.
  • Brújula con Alineación Háptica: Siente una vibración en tu muñeca en el momento exacto en que apuntas hacia tu vehículo.
  • Alerta de Parquímetro: Fija tiempos límite y recibe alertas antes de que expire tu tiempo, con un anillo de progreso visual en tiempo real.
  • 100% Autónomo: Funciona de forma independiente en tu reloj inteligente sin depender de conexión permanente al teléfono.
  • Complicación y Tile incluidos: Consulta tu piso y zona con solo deslizar en la esfera de tu reloj.

  PRIVACIDAD Y CONTROL TOTAL
  • Procesamiento 100% local: Tus datos de ubicación nunca salen de tu reloj.
  • Sin publicidad, sin suscripciones y sin rastreo.
  ```

---

### English (United States)

- **App Name** (max. 30 characters):
  `ParkWatch: Find My Car`

- **Short description** (max. 80 characters):
  `Locate your parked vehicle in 1 tap. Haptic compass & meter alerts on your wrist.`

- **Full description** (max. 4000 characters):
  ```text
  ParkWatch is the standalone vehicle location tool designed specifically for Wear OS smartwatches. Save and find your parking spot directly from your wrist—no phone needed.

  WRIST-FIRST WEAR OS DESIGN
  • Quick Registration: Select floor, zone, row, and optional spot using ergonomic touch chips designed for round displays.
  • One-Tap GPS Save: Capture your exact parking coordinates in seconds.
  • Voice Input: Speak your parking location naturally for instant hands-free entry.
  • Haptic Alignment Compass: Feel a subtle wrist vibration the instant you are pointing directly towards your vehicle.
  • Parking Meter Timer: Set reminders and watch a live circular progress ring count down, alerting you before time runs out.
  • Standalone Experience: Fully autonomous on your smartwatch without requiring a companion phone app.
  • Tile and Complication Included: Glance at your saved floor and zone directly from your watch face.

  PRIVACY FIRST
  • 100% On-Device Processing: Your location data never leaves your watch.
  • No ads, no tracking, and no external data collection.
  ```

---

## 2. Formulario de Seguridad de los Datos (Data Safety Form)

En la Google Play Console, ve a **Contenido de la app → Seguridad de los datos** y responde de la siguiente manera:

1. **¿Tu app recopila o comparte alguno de los tipos de datos de usuario requeridos?**
   - **Respuesta:** Sí (solo si se considera la lectura de sensores, pero especifica que NO se comparten).
2. **¿Los datos que recopila tu app se transfieren a través de una conexión segura?**
   - **Respuesta:** Sí.
3. **¿Ofreces a los usuarios una forma de solicitar la eliminación de sus datos?**
   - **Respuesta:** Sí (mediante el botón de borrado de ubicación / borrado de historial dentro de la app o al desinstalar).

### Desglose por Permisos:

- **Ubicación (Ubicación precisa / Precise location):**
  - **¿Se recopila o se comparte?** Recopilada (solo localmente en el dispositivo).
  - **¿Se procesa efímeramente?** No (se almacena localmente en SharedPreferences para la brújula).
  - **¿Los datos son necesarios o el usuario puede elegir si recopilarlos?** El usuario puede elegir (solicitado en tiempo de ejecución).
  - **Finalidad:** Funcionalidad de la aplicación (navegación y brújula hacia el auto).

- **Audio (Grabaciones de voz o sonido):**
  - **¿Se recopila o se comparte?** No recopilada fuera del dispositivo. El audio se procesa de forma efímera para el reconocimiento de voz y se descarta inmediatamente.

- **Datos de salud, financieros, contactos, fotos:**
  - **Respuesta:** Ninguno (No se recopilan).

---

## 3. Requisitos de Recursos Gráficos en Play Console

- **Icono de la app:** 512 × 512 px, PNG de 32 bits con fondo transparente o sólido.
- **Gráfico de funciones (Feature Graphic):** 1024 × 500 px, PNG o JPEG.
- **Capturas de pantalla de Wear OS:**
  - Resolución: 384 × 384 px o 454 × 454 px (relación de aspecto 1:1).
  - Mínimo 1 captura, recomendado entre 4 y 8 capturas mostrando:
    1. Pantalla principal vacía (*¿Dónde estacionaste?* con halo OLED).
    2. Flujo de guardado paso a paso (Chips táctiles de piso y zona).
    3. Tarjeta de auto estacionado con anillo de temporizador.
    4. Brújula táctica guiando al auto.
    5. Tile de acceso rápido en la esfera del reloj.

---

## 4. Archivo para Subir a Play Console

- **Ruta del artefacto:**
  `app/build/outputs/bundle/release/app-release.aab`
- **Firma digital:**
  Firmado con `keystore/parkwatch-release.jks`.
- **Canal recomendado para la primera subida:**
  **Prueba interna (*Internal testing*)** — permite instalar la app inmediatamente en tu propio reloj mediante un enlace de Google Play antes de abrirla a pruebas abiertas o producción.
