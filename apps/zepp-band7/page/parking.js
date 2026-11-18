import { bg, text, button, go, appState, t, hapticFeedback } from '../shared/ui';
import { finishParking as finishLocal, deleteActiveParking, loadParking, saveParking } from '../services/local-parking';
import { formatDuration } from '../shared/format';
let finishArmed = false;
let deleteArmed = false;
function backButton(clickFunc) {
  return hmUI.createWidget(hmUI.widget.BUTTON, { x: 14, y: 209, w: 164, h: 40, radius: 14, normal_color: 0x333333, press_color: 0x4a4a4a, text: t('Volver', 'Back'), text_size: 14, color: 0xffffff, click_func: clickFunc });
}
Page({
  build() {
    bg(); const s = appState(); s.error = ''; const data = loadParking(); s.activeParking = data.active; s.history = data.history; const p = data.active;
    text(8, 16, 176, 28, t('MI PARQUEO', 'MY PARKING'), 18, 0x35e08f);
    text(12, 54, 168, 36, p ? p.space : t('Sin parqueo activo', 'No active parking'), p ? 30 : 16, p ? undefined : 0xb0b0b0);
    if (p) {
      text(12, 94, 168, 22, `${t('Zona', 'Area')} ${p.area || 'A'} · ${t('Piso', 'Floor')} ${p.floor}`, 15);
      text(12, 119, 168, 22, `${t('Fila', 'Row')} ${p.row} · ${t('Puesto', 'Spot')} ${p.space}`, 15);
      text(12, 144, 168, 22, `${t('Aprox.', 'Approx.')} ${p.distance} m`, 14, 0x35e08f);
      text(12, 166, 168, 17, t('Distancia ingresada manualmente', 'Distance entered manually'), 9, 0xb0b0b0);
      const startedAt = Number(p.startedAt) || Date.now();
      const elapsedLabel = t('Tiempo activo', 'Parked for');
      text(12, 185, 168, 20, `${elapsedLabel}: ${formatDuration((Date.now() - startedAt) / 60000)}`, 12);
      backButton(() => { finishArmed = false; deleteArmed = false; go('page/index'); });
      button(258, t('Editar ubicación', 'Edit location'), () => { s.editParking = true; finishArmed = false; deleteArmed = false; go('page/new-parking'); });
      button(310, finishArmed ? t('Tocar para confirmar', 'Tap to confirm') : t('Finalizar parqueo', 'Finish parking'), () => { if (!finishArmed) { finishArmed = true; go('page/parking'); return; } const next = finishLocal(data); if (saveParking(next)) { hapticFeedback(); finishArmed = false; s.activeParking = null; s.history = next.history; s.state = 'PARKING_FINISHED'; go('page/parking'); } }, 0x652727);
      button(362, deleteArmed ? t('Confirmar eliminación', 'Confirm delete') : t('Eliminar parqueo', 'Delete parking'), () => { if (!deleteArmed) { deleteArmed = true; finishArmed = false; go('page/parking'); return; } const next = deleteActiveParking(data); if (saveParking(next)) { hapticFeedback(); deleteArmed = false; finishArmed = false; s.activeParking = null; s.editParking = false; go('page/parking'); } }, 0x5a3330);
    }
    else { text(12, 120, 168, 32, t('No hay parqueo activo', 'No active parking'), 16, 0xb0b0b0); button(264, t('Guardar parqueo', 'Save parking'), () => go('page/new-parking')); }
    if (!p) button(410, t('Volver', 'Back'), () => { finishArmed = false; deleteArmed = false; go('page/index'); }, 0x333333);
  }
});
