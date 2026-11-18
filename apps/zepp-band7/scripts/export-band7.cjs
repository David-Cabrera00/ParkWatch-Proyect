const fs = require('fs');
const path = require('path');
const dist = path.resolve(__dirname, '..', 'dist');
if (!fs.existsSync(dist)) throw new Error('dist/ no existe. Ejecuta npm run build primero.');
const files = fs.readdirSync(dist).filter((name) => name.toLowerCase().endsWith('.bin'));
if (files.length !== 1) throw new Error('No se encontró una conversión BIN documentada/verificable; no se generará un archivo falso.');
console.log(path.join(dist, files[0]));
