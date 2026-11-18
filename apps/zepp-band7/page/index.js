import { bg, text, button, go } from '../shared/ui';
import { loadParking } from '../services/local-parking';

Page({
  build() {
    bg();
    const data = loadParking();
    const active = data.active;
    text(8, 18, 176, 32, 'PARQUEO BAND 7', 20, 0x35e08f);
    text(12, 58, 168, 24, 'MODO OFFLINE', 14, 0xb0b0b0);
    if (active) {
      text(12, 88, 168, 28, `Zona ${active.area || 'A'} · Piso ${active.floor}`, 16, 0xffffff);
      text(12, 118, 168, 28, `Fila ${active.row} · Puesto ${active.space}`, 16, 0xffffff);
      text(12, 148, 168, 26, `Aprox. ${active.distance} m`, 15, 0x35e08f);
      text(12, 176, 168, 22, 'PARQUEO ACTIVO', 13, 0x35e08f);
      button(205, 'Ver mi parqueo', () => go('page/parking'));
    } else {
      text(12, 106, 168, 32, 'Sin parqueo activo', 17, 0xb0b0b0);
      button(180, 'Guardar parqueo', () => go('page/new-parking'));
    }
    button(275, 'Historial local', () => go('page/history'));
    button(370, 'Menú', () => go('page/menu'), 0x333333);
  },
});
