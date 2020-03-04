import router from './router'
import store from './store'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import {getToken, removeToken} from '@/utils/loginToken' // get token from cookie
import defaultSettings from '@/settings'
import request from '@/utils/request'
import {buildMenu, buildRoute} from "@/router"

NProgress.configure({showSpinner: false}) // NProgress Configuration

const whiteList = ['/login'] // no redirect whitelist

router.beforeEach(async (to, from, next) => {
  NProgress.start();
  // set page title
  document.title = `${defaultSettings.title}-${to.meta.title || ''}`;

  // 登录信息与用户初始化信息（包含菜单，权限，路由，数据字典等）
  const token = getToken()
  let userData = store.getters.userData;


  if (token && to.path === '/login') {                  //如果是已经登录过,并且请求的是登录页面，则跳转到主页
    next({path: '/'})

  } else if (token && userData) {                       //如果已经登录过,并且也已经获取到了用户数据
    next();
    return;

  } else if (token && !userData) {                      //如果已经登录过，但未获取到用户数据
    try {
      if ((userData = await getUserData(token))) {
        next({...to, replace: true})
        return;
      }
    } catch (err) {
    }
    //如果获取数据失败
    if (!userData) {
      removeToken();
      next(`login?redirect=${to.path}`)
    }

  } else {                                              //如果未登录
    if (whiteList.indexOf(to.path) !== -1) {            //不需要登录的地址（比如登录页面）
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }

  NProgress.done();
})

router.afterEach(() => {
  NProgress.done()
})


function getUserData(token) {
  let p1 = request.get('/user/info', {params: {token}});
  let p2 = request.get('/dataDictionary/listAllDataDictionaryVO');

  return Promise.all([p1, p2])
    .then(([{data: {name, functions}}, {data: dictionaryList}]) => {
      if (!dictionaryList || !functions) {
        //如果没有获取到数据字典或者用户功能列表,则不设置userData
        return;
      }
      let routes = buildRoute(functions)
      router.addRoutes(routes);
      let menus = buildMenu(functions)
      let dictionary = dictionaryList.reduce((map, item) => {
        map[item.dataName] = item.dataInfo;
        return map;
      }, {})
      let permissionFlags = functions.map(f => f.permissionFlag);
      return store.dispatch('setUserData', {name, menus, routes, permissionFlags: new Set(permissionFlags), dictionary})
    })
}
