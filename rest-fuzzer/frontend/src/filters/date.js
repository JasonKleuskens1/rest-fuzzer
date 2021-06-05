import Vue from 'vue'
import moment from 'moment'

Vue.filter('date', function(value, pattern='DD-MM-YYYY HH:mm:ss') {
  return (value === null || value === '' || value === '0') ? '-' : moment(value, moment.ISO_8601).format(pattern)
})

Vue.filter('dateShort', function(value, pattern='DD-MM HH:mm') {
  return (value === null || value === '' || value === '0') ? '-' : moment(value, moment.ISO_8601).format(pattern)
})