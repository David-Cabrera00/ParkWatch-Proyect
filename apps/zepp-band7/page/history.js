import { bg, text, button, go, appState, t } from '../shared/ui';
import { loadParking } from '../services/local-parking';
import { formatDuration, formatLocalDateTime } from '../shared/format';

Page({
  build() {
    bg();
    const history = loadParking().history;
    appState().history = history;
    text(8, 16, 176, 28, t('HISTORIAL LOCAL', 'LOCAL HISTORY'), 18, 0x35e08f);
    if (!history.length) {
      text(10, 100, 172, 40, t('Sin registros guardados', 'No saved records'), 15, 0xb0b0b0);
    } else {
      history.slice(0, 5).forEach((parking, index) => {
        const y = 58 + index * 66;
        text(10, y, 172, 23, formatLocalDateTime(parking.endedAt || parking.startedAt), 12, 0xb0b0b0);
        const location = `${t('Zona', 'Area')} ${parking.area || 'A'} · P${parking.floor} F${parking.row} #${parking.space}`;
        text(10, y + 23, 172, 25, `${location} · ${formatDuration(parking.durationMinutes)}`, 11);
      });
    }
    button(400, t('Volver', 'Back'), () => go('page/menu'), 0x333333);
  },
});
