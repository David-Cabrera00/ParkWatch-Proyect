import { COLORS } from './constants';
import { loadSettings } from '../services/local-parking';
let hapticSensor = null;
export function t(es, en) { return loadSettings().language === 'en' ? en : es; }
export function bg() { const light = loadSettings().theme === 'light'; hmUI.createWidget(hmUI.widget.FILL_RECT, { x: 0, y: 0, w: 192, h: 490, color: light ? 0xf0f3f1 : COLORS.bg }); }
export function themeTextColor(color) {
  if (loadSettings().theme !== 'light') return color === undefined ? COLORS.white : color;
  const lightColors = {
    [COLORS.white]: 0x17231d,
    [COLORS.muted]: 0x53635b,
    [COLORS.green]: 0x16784b,
    [COLORS.blue]: 0x17669a,
    [COLORS.red]: 0xa52e35,
  };
  return color === undefined ? 0x17231d : (lightColors[color] || color);
}
export function text(x, y, w, h, value, size = 16, color, click_func) { const props = { x, y, w, h, text: String(value), text_size: size, color: themeTextColor(color), align_h: hmUI.align.CENTER_H, align_v: hmUI.align.CENTER_V }; if (typeof click_func === 'function') props.click_func = click_func; return hmUI.createWidget(hmUI.widget.TEXT, props); }
export function button(y, label, click_func, color) { const light = loadSettings().theme === 'light'; return hmUI.createWidget(hmUI.widget.BUTTON, { x: 14, y, w: 164, h: 50, radius: 18, normal_color: color || (light ? 0x287653 : 0x163a2b), press_color: 0x285843, text: label, text_size: 15, color: 0xffffff, click_func }); }
export function hapticFeedback() {
  try {
    if (typeof hmSensor === 'undefined' || !hmSensor.id || typeof hmSensor.id.VIBRATE === 'undefined') return;
    if (!hapticSensor) hapticSensor = hmSensor.createSensor(hmSensor.id.VIBRATE);
    hapticSensor.stop();
    hapticSensor.scene = 23;
    hapticSensor.start();
  } catch (_) {}
}
// Replace the current page so repeated menu/back/confirmation actions do not
// accumulate an unbounded navigation stack on the Band 7.
export function go(url) { try { hmApp.reloadPage({ url }); } catch (_) {} }
export function appState() { const app = typeof getApp === 'function' ? getApp() : null; return (app && app.globalData) || globalThis.__PARQUEO_GLOBAL__ || {}; }
