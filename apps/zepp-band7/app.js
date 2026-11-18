const initial = {
  mode: 'OFFLINE',
  state: 'IDLE',
  connection: 'LOCAL',
  availability: null,
  activeParking: null,
  history: [],
  error: '',
  lastSyncAt: 0,
};

App({
  globalData: initial,
  onCreate() {
    this.globalData = this.globalData || initial;
    this._options = this._options || {};
    this._options.globalData = this.globalData;
    globalThis.__PARQUEO_GLOBAL__ = this.globalData;
  },
  onDestroy() {},
});
