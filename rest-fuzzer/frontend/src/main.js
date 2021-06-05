/* vuejs */
import Vue from 'vue';
import App from "./app.vue";

import router from './router';
import store from "./shared/store";

/* clipboard */
import VueClipboard from 'vue-clipboard2'
 
Vue.use(VueClipboard)

/* timers */
import VueTimers from 'vue-timers'

Vue.use(VueTimers)

/* bootstrap */
import { BootstrapVue, IconsPlugin }  from 'bootstrap-vue'

Vue.use(BootstrapVue)
Vue.use(IconsPlugin)

/* filters */
import './filters/date'
import './filters/other'

/* other */
Vue.config.productionTip = false;

/* init */
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");