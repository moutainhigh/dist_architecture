import vue from '../../main.js'

const tagsView = {
  state: {
    visitedRoutes: [],
  },
  mutations: {
    ADD_VISITED_ROUTE: (state, route) => {
      if (state.visitedRoutes.some(r => r.path === route.path)) return

      let matchedComponents = vue.$router.getMatchedComponents(route)
      let componentName = matchedComponents && matchedComponents[1] && matchedComponents[1].name
      if (!componentName) {
        return;//二级路由没有匹配到组件，不添加标签
      }

      let cache = true;
      let nameRepeatRoutes = state.visitedRoutes.filter(r => r.cachedComponentName === componentName && r.path !== route.path)
      //如果发现有重名的组件，则设置keep-alive全部失效
      nameRepeatRoutes.forEach(repeatRoute => {
        console.error(`发现不同的组件使用到了相同的组件名称,强制keep-alive缓存失效,请修改组件名称,componentName=${componentName},
        permissionFlag1=${repeatRoute.meta.permissionFlag},permissionFlag2=${route.meta.permissionFlag}`)
        repeatRoute.cachedComponentName = undefined;
        cache = false;
      })

      let visitedRoute = Object.assign(cache && (!route.meta.noCache) ? {cachedComponentName: componentName} : {}, route, {title: route.meta.title || 'no-name'})
      state.visitedRoutes.push(visitedRoute)
    },

    DEL_VISITED_ROUTE: (state, route) => {
      for (const [i, r] of state.visitedRoutes.entries()) {
        if (r.path === route.path) {
          state.visitedRoutes.splice(i, 1)
          break
        }
      }
    },

    DEL_OTHERS_VISITED_ROUTES: (state, retainRoute) => {
      state.visitedRoutes = state.visitedRoutes.filter(r => {
        return r.meta.affix || r.path === retainRoute.path
      })
    },

    DEL_ALL_VISITED_ROUTES: state => {
      // keep affix tags
      state.visitedRoutes = state.visitedRoutes.filter(r => r.meta.affix)
    },

    DEL_VISITED_ROUTE_CACHE: (state, route) => {
      state.visitedRoutes.filter(p => p.path === route.path)
        .forEach(route => route.cachedComponentName = undefined)
    }

  },
  actions: {
    addVisitedRoute({commit, state}, route) {
      return new Promise(resolve => {
        commit('ADD_VISITED_ROUTE', route)
        resolve([...state.visitedRoutes])
      })
    },
    delVisitedRoute({commit, state}, route) {
      return new Promise(resolve => {
        commit('DEL_VISITED_ROUTE', route)
        resolve([...state.visitedRoutes])
      })
    },
    delOthersVisitedRoutes({commit, state}, route) {
      return new Promise(resolve => {
        commit('DEL_OTHERS_VISITED_ROUTES', route)
        resolve([...state.visitedRoutes])
      })
    },
    delAllVisitedRoutes({commit, state}) {
      return new Promise(resolve => {
        commit('DEL_ALL_VISITED_ROUTES')
        resolve([...state.visitedRoutes])
      })
    },
    delVisitedRouteCache({commit, state}, route) {
      return new Promise(resolve => {
        commit('DEL_VISITED_ROUTE_CACHE', route)
        resolve([...state.visitedRoutes])
      })
    }
  }
}

export default tagsView
