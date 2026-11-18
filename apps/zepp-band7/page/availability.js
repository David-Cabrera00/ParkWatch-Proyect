import { bg, text, button, go, appState, service } from '../shared/ui';
Page({
  build() {
    bg(); const s = appState(); text(8, 16, 176, 28, 'DISPONIBILIDAD', 18, 0x35e08f);
    const d = s.availability; text(10, 62, 172, 36, d ? `Libres   ${d.free}` : 'Cargando…', 22, 0xffffff); text(10, 106, 172, 30, d ? `Ocupados   ${d.occupied}` : 'Consulta en curso', 16, 0xb0b0b0); text(10, 144, 172, 30, d ? `Total   ${d.total}` : '', 16, 0xb0b0b0);
    if (d && d.zones) d.zones.slice(0, 2).forEach((z, i) => text(12, 206 + i * 40, 168, 30, `Zona ${z.name}: ${z.free} libres`, 15, 0x70b7ff));
    button(330, 'Actualizar', async () => { try { s.state = 'LOADING'; s.availability = await service().getAvailability(); go('page/availability'); } catch (e) { s.error = e.message; go('page/availability'); } });
    button(390, 'Volver', () => go('page/index'), 0x333333);
  }
});
