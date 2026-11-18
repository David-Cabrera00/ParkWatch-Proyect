# Parqueo Band 7 — Zepp OS

Aplicación offline para Xiaomi Smart Band 7 International. Esta es una app Zepp OS independiente, separada de la aplicación Wear OS que vive en la raíz del repositorio.

## Funciones

- Guardar la ubicación manual del parqueo: zona, piso, fila, puesto y distancia aproximada.
- Consultar, editar, finalizar y eliminar el parqueo activo.
- Mantener historial local con fecha, hora y duración.
- Cambiar idioma español/inglés y tema claro/oscuro.
- Confirmación háptica al guardar, finalizar o borrar.
- Persistir los datos en la pulsera mediante `hmFS`; el flujo principal no necesita backend ni Bluetooth.

## Compilar y previsualizar

Requiere Node.js y npm. Desde esta carpeta:

```powershell
npm ci
npm run build
```

El paquete `.zab` queda en `dist/`. Para generar un QR temporal de vista previa en Zepp:

```powershell
npm run preview
```

En Zepp, activa Developer Mode y usa **Scan** para leer el QR. El QR es temporal y sirve para vista previa.

## Target

- Zepp OS CLI: `@zeppos/zeus-cli` 1.8.2
- Target: `mi-band7-intl`
- Device source: `263`
