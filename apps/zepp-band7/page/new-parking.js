import { bg, text, button, go, appState, t, themeTextColor, hapticFeedback } from '../shared/ui';
import { createParkingRecord, loadParking, saveParking } from '../services/local-parking';

function step(y, label, key, min, max, state, update) {
  text(8, y, 78, 30, label, 14, 0xb0b0b0);
  const valueWidget = text(88, y, 34, 30, state[key], 18);
  const changeBy = delta => {
    state[key] = Math.max(min, Math.min(max, state[key] + delta));
    update(valueWidget, state[key], 88, y, 34, 30);
  };
  buttonAt(130, y - 2, 26, 30, '−', () => changeBy(-1));
  buttonAt(160, y - 2, 26, 30, '+', () => changeBy(1));
}
function buttonAt(x, y, w, h, label, click_func) { return hmUI.createWidget(hmUI.widget.BUTTON, { x, y, w, h, radius: 9, normal_color: 0x254d3c, press_color: 0x39765a, text: label, text_size: 16, color: 0xffffff, click_func }); }
let draft = { area: 'A', floor: 1, row: 1, space: 1, distance: 0 };

Page({
  build() {
    bg(); const data = loadParking(); const app = appState(); const editing = !!(app.editParking && data.active);
    if (data.active && !editing) { text(8, 70, 176, 52, t('Ya tienes un parqueo activo', 'You already have active parking'), 18, 0xb0b0b0); button(180, t('Ver parqueo', 'View parking'), () => go('page/parking')); button(398, t('Volver', 'Back'), () => go('page/index'), 0x333333); return; }
    if (editing) draft = { area: data.active.area || 'A', floor: data.active.floor, row: data.active.row, space: data.active.space, distance: data.active.distance };
    const state = draft;
    text(8, 18, 176, 28, editing ? t('EDITAR UBICACIÓN', 'EDIT LOCATION') : t('GUARDAR PARQUEO', 'SAVE PARKING'), 16, 0x35e08f);
    const update = (valueWidget, value, x, y, w, h, textSize = 18) => {
      draft = state;
      // Zepp OS 1.0 supports MORE on setProperty; newer TEXT setters are not reliable on Band 7.
      if (valueWidget && typeof valueWidget.setProperty === 'function') {
        valueWidget.setProperty(hmUI.prop.MORE, {
          x, y, w, h, text: String(value), text_size: textSize,
          color: themeTextColor(),
          align_h: hmUI.align.CENTER_H,
          align_v: hmUI.align.CENTER_V,
        });
      }
    };
    const areaWidget = text(8, 54, 100, 28, t('Zona', 'Area') + ': ' + state.area, 14);
    buttonAt(130, 52, 26, 30, '−', () => { state.area = String.fromCharCode(Math.max(65, state.area.charCodeAt(0) - 1)); update(areaWidget, t('Zona', 'Area') + ': ' + state.area, 8, 54, 100, 28, 14); });
    buttonAt(160, 52, 26, 30, '+', () => { state.area = String.fromCharCode(Math.min(67, state.area.charCodeAt(0) + 1)); update(areaWidget, t('Zona', 'Area') + ': ' + state.area, 8, 54, 100, 28, 14); });
    step(91, t('Piso', 'Floor'), 'floor', 0, 20, state, update);
    step(135, t('Fila', 'Row'), 'row', 1, 50, state, update);
    step(179, t('Puesto', 'Spot'), 'space', 1, 200, state, update);
    step(223, t('Metros aprox.', 'Approx. meters'), 'distance', 0, 999, state, update);
    text(8, 258, 176, 22, t('Distancia aproximada ingresada', 'Approximate distance entered'), 10, 0xb0b0b0);
    button(292, editing ? t('Guardar cambios', 'Save changes') : t('Guardar ubicación', 'Save location'), () => { try { const record = createParkingRecord(state); const active = editing ? { ...data.active, ...record, id: data.active.id, startedAt: data.active.startedAt } : record; const next = { active, history: data.history }; if (saveParking(next)) { hapticFeedback(); draft = { area: 'A', floor: 1, row: 1, space: 1, distance: 0 }; app.editParking = false; app.activeParking = active; go('page/parking'); } } catch (_) {} });
    button(382, t('Cancelar', 'Cancel'), () => { draft = { area: 'A', floor: 1, row: 1, space: 1, distance: 0 }; app.editParking = false; go(editing ? 'page/parking' : 'page/index'); }, 0x333333);
  }
});
