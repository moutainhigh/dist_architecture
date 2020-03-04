export function formatMoney(value) {
  if (!value && value !== '0' && value !== 0) {
    return '';
  } else if (value === '-') {
    return value;
  } else {
    const decimal = Number(value).toFixed(2).split('.')[1];
    return (Math.floor(value).toLocaleString('en-US') + '.' + decimal);
  }
}

