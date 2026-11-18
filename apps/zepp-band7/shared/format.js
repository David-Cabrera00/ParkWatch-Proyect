export function formatDuration(totalMinutes) {
  const minutes = Math.max(0, Math.floor(Number(totalMinutes) || 0));
  const days = Math.floor(minutes / 1440);
  const hours = Math.floor((minutes % 1440) / 60);
  const remainder = minutes % 60;
  if (days) return `${days} d ${hours} h`;
  if (hours) return `${hours} h ${remainder} min`;
  return `${remainder} min`;
}

export function formatLocalDateTime(timestamp) {
  const date = new Date(Number(timestamp));
  if (!Number.isFinite(date.getTime())) return '--/--/---- --:--';
  const pad = value => value < 10 ? `0${value}` : String(value);
  return `${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
}
