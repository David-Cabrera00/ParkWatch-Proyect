import { demoData } from '../mocks/parking-data';

function validAvailability(value) { return value && Number.isInteger(value.total) && Number.isInteger(value.occupied) && Number.isInteger(value.free) && value.total >= 0 && value.occupied >= 0 && value.free >= 0 && value.occupied + value.free === value.total; }
function validParking(value) { return value === null || (value && typeof value.id === 'string' && typeof value.space === 'string' && typeof value.status === 'string'); }
export function createParkingService({ mode = 'DEMO', fetchImpl = globalThis.fetch, baseUrl = '' } = {}) {
  let finishing = false;
  const real = async (path, options) => { if (!fetchImpl || !baseUrl) throw new Error('REAL mode requires baseUrl and fetch'); const response = await fetchImpl(baseUrl + path, { headers: { Accept: 'application/json' }, ...options }); if (!response.ok) throw new Error('HTTP ' + response.status); return response.json(); };
  return {
    mode,
    async getAvailability() { const data = mode === 'DEMO' ? demoData.availability : await real('/availability'); if (!validAvailability(data)) throw new Error('Invalid availability response'); return data; },
    async getActiveParking() { const data = mode === 'DEMO' ? demoData.activeParking : await real('/parking/active'); if (!validParking(data)) throw new Error('Invalid active parking response'); return data; },
    async getHistory() { const data = mode === 'DEMO' ? demoData.history : await real('/parking/history'); if (!Array.isArray(data)) throw new Error('Invalid history response'); return data; },
    async finishParking(id) { if (finishing) throw new Error('Operation already in progress'); if (!id) throw new Error('Missing parking id'); finishing = true; try { const result = mode === 'DEMO' ? { ok: true, id } : await real('/parking/' + encodeURIComponent(id) + '/finish', { method: 'POST' }); if (!result || result.ok !== true) throw new Error('Server did not confirm finish'); return result; } finally { finishing = false; } }
  };
}
