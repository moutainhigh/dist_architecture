import Vue from 'vue';
const dayjs = require('dayjs');

Vue.filter('datefmt', function (input, fmtstring) {
  return !input ? '' : dayjs(input).format(fmtstring);
});
