import store from '@/store'

export default {
  inserted(el, binding, vnode) {
    let permissionFlags = store.getters.userData && store.getters.userData.permissionFlags;
    const {value} = binding
    if (value && (!permissionFlags || !permissionFlags.has(value))) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}
