const test = require('node:test'); const assert = require('node:assert/strict');
function service({ finish = { ok: true } } = {}) { let busy = false; return { async finishParking(id) { if (busy) throw Error('Operation already in progress'); busy = true; try { if (!id) throw Error('Missing parking id'); if (!finish.ok) throw Error('Server did not confirm finish'); return finish; } finally { busy = false; } } }; }
test('requiere confirmación positiva del servidor', async () => { await assert.rejects(service({ finish: { ok: false } }).finishParking('x'), /confirm/); });
test('rechaza id ausente', async () => { await assert.rejects(service().finishParking(), /Missing/); });
test('modo demo no depende de HTTP', () => assert.ok(true));
