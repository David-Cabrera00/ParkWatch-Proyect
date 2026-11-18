const test = require('node:test'); const assert = require('node:assert/strict');
function roundTrip(msg) { const allowed = ['GET_AVAILABILITY','AVAILABILITY_RESPONSE','GET_ACTIVE_PARKING','ACTIVE_PARKING_RESPONSE','FINISH_PARKING','FINISH_PARKING_RESPONSE','PING','PONG','ERROR']; if (!allowed.includes(msg.type)) throw Error('Invalid message type'); return JSON.parse(JSON.stringify({ v: 1, requestId: String(msg.requestId || ''), type: msg.type, payload: msg.payload ?? null })); }
test('codifica y decodifica mensajes', () => assert.deepEqual(roundTrip({ type: 'PING', requestId: 4, payload: { x: 1 } }), { v: 1, requestId: '4', type: 'PING', payload: { x: 1 } }));
test('rechaza tipos desconocidos', () => assert.throws(() => roundTrip({ type: 'NOPE' })));
