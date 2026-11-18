const fs = require('node:fs');
const path = require('node:path');
const { spawnSync } = require('node:child_process');
const mode = process.argv[2];
if (!['build', 'preview'].includes(mode)) throw new Error('Use build or preview');
const bundle = require.resolve('@zeppos/zpm/lib/node.js');
const original = fs.readFileSync(bundle, 'utf8');
const needle = 'let g=A[0]||0;';
if (original.split(needle).length !== 2) throw new Error('Unexpected Zeus packager version; refusing to patch node_modules');
const guard = needle + 'if(!(g>0))return yield r().move(o,t);';
const qrBundle = require.resolve('@zeppos/zeus-cli/modules/build.js');
const originalQr = fs.readFileSync(qrBundle, 'utf8');
const qrNeedle = 'qrcode_terminal_1.default.generate(res.url, { small: true });';
if (originalQr.split(qrNeedle).length !== 2) throw new Error('Unexpected Zeus QR renderer version; refusing to patch node_modules');
const largeQr = qrNeedle.replace('small: true', 'small: false');
try {
  fs.writeFileSync(bundle, original.replace(needle, guard));
  fs.writeFileSync(qrBundle, originalQr.replace(qrNeedle, largeQr));
  const cli = path.join(path.dirname(require.resolve('@zeppos/zeus-cli/package.json')), 'bin', 'main.js');
  const result = spawnSync(process.execPath, [cli, mode, '-t', 'mi-band7-intl'], { stdio: 'inherit', cwd: path.join(__dirname, '..') });
  if (result.error) throw result.error;
  process.exitCode = result.status === null ? 1 : result.status;
} finally {
  fs.writeFileSync(bundle, original);
  fs.writeFileSync(qrBundle, originalQr);
}
