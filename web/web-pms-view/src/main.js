import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import ElementUI from 'element-ui'
// import 'element-ui/lib/theme-chalk/index.css'
import '@/mini-element-theme/index.css'
import i18n from './lang'

import '@/styles/index.scss' // global css
import App from './App'
import store from './store'
import router from './router'

import '@/filter/filter.js'; // 过滤器
import '@/icons' // icon
import '@/permission' // permission control
import VPermission from '@/directive/permission'
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */
import {mockXHR} from '../mock'

//install v-permission
VPermission.install(Vue)


if (process.env.NODE_ENV === 'production') {
  mockXHR()
}

// set ElementUI lang to EN
Vue.use(ElementUI)

Vue.config.productionTip = false

export default new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: h => h(App)
})

Vue.prototype.$dict = (dataName) => {
  let dictionary = store.getters.userData && store.getters.userData.dictionary;
  if (dictionary && dictionary[dataName]) {
    return dictionary[dataName]
  } else {
    return []
  }
};


Vue.prototype.$dictFlag = (dataName, dataFlag) => {
  let dictionary = store.getters.userData && store.getters.userData.dictionary;
  if (dictionary && dictionary[dataName]) {
    for (let item of dictionary[dataName]) {
      if (item.flag === String(dataFlag)) {
        return item;
      }
    }
  }
  return {}
};

Vue.prototype.$dictCode = (dataName, dataCode) => {
  let dictionary = store.getters.userData && store.getters.userData.dictionary;
  if (dictionary && dictionary[dataName]) {
    for (let item of dictionary[dataName]) {
      if (item.code === String(dataCode)) {
        return item;
      }
    }
  }
  return {}
}

