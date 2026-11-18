import { isHmTimerDefined } from "./js-module";
import { getGlobal } from "./global";

const globalNS = getGlobal();

if (isHmTimerDefined()) {
  if (typeof globalNS.clearTimeout === 'undefined') {
    globalNS.clearTimeout = function clearTimeout(timerRef) {
      if (timerRef) timer.stopTimer(timerRef);
    };
  }

  if (typeof globalNS.setTimeout === 'undefined') {
    globalNS.setTimeout = function setTimeout(func, delay) {
      let timerRef;
      timerRef = timer.createTimer(Math.max(1, delay || 1), Number.MAX_SAFE_INTEGER, () => {
        globalNS.clearTimeout(timerRef);
        if (func) func();
      }, {});
      return timerRef;
    };
  }

  if (typeof globalNS.clearInterval === 'undefined') {
    globalNS.clearInterval = function clearInterval(timerRef) {
      if (timerRef) timer.stopTimer(timerRef);
    };
  }

  if (typeof globalNS.setInterval === 'undefined') {
    globalNS.setInterval = function setInterval(func, delay) {
      return timer.createTimer(1, Math.max(1, delay || 1), () => {
        if (func) func();
      }, {});
    };
  }

  if (typeof globalNS.clearImmediate === 'undefined') {
    globalNS.clearImmediate = function clearImmediate(timerRef) {
      if (timerRef) timer.stopTimer(timerRef);
    };
  }

  if (typeof globalNS.setImmediate === 'undefined') {
    globalNS.setImmediate = function setImmediate(func) {
      let timerRef;
      timerRef = timer.createTimer(1, Number.MAX_SAFE_INTEGER, () => {
        globalNS.clearImmediate(timerRef);
        if (func) func();
      }, {});
      return timerRef;
    };
  }
}
