const TYPES = ['GET_AVAILABILITY','AVAILABILITY_RESPONSE','GET_ACTIVE_PARKING','ACTIVE_PARKING_RESPONSE','FINISH_PARKING','FINISH_PARKING_RESPONSE','PING','PONG','ERROR'];
export const MESSAGE_TYPES = Object.freeze(TYPES.reduce((a, x) => { a[x] = x; return a; }, {}));
export function encodeMessage(message) {
  if (!message || typeof message !== 'object' || !TYPES.includes(message.type)) throw new Error('Invalid message type');
  return JSON.stringify({ v: 1, requestId: String(message.requestId || ''), type: message.type, payload: message.payload === undefined ? null : message.payload });
}
export function decodeMessage(raw) {
  const value = typeof raw === 'string' ? JSON.parse(raw) : raw;
  if (!value || value.v !== 1 || !TYPES.includes(value.type)) throw new Error('Invalid message');
  return value;
}
