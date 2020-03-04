import {buildMenu, buildRoute, constantRoutes} from '@/router'

const permission = {
  state: {
    routes: JSON.parse(sessionStorage.getItem('routes')) || [],
    menus: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.routes = constantRoutes.concat(routes)
      /* XXX: 解决权限页面刷新，vuex数据刷新跳404页面的问题。原因：路由动态加载没有完成，若404存在，则直接跳404，因此404页面需要动态加载 */
      let fileRoute = {path: '*', redirect: '/404', hidden: true};
      state.routes = state.routes.concat(fileRoute)
      sessionStorage.setItem('routes', JSON.stringify(state.routes))
    },
    SET_MENUS: (state, menus) => {
      state.menus = menus;
    }
  },
  actions: {
    GenerateRoutesAndMenus({commit}, {functions}) {
      return new Promise(resolve => {
        let accessedRoutes = buildRoute(functions)
        let accessedMenus = buildMenu(functions)
        commit('SET_ROUTES', accessedRoutes)
        commit('SET_MENUS', accessedMenus)
        resolve({accessedRoutes, accessedMenus})
      })
    },
    ResetRoutesAndMenus({commit}) {
      return new Promise(resolve => {
        commit('SET_ROUTES', [])
        commit('SET_MENUS', [])
        resolve({accessedRoutes: [], accessedMenus: []})
      })
    }

  }
}

export default permission
