import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'

Vue.use(Router)

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/common/Login'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/common/404'),
    hidden: true
  },


  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/common/Redirect')
      }
    ]
  },
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({y: 0}),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router


let routesInfo = {
  'pms:operator:view': () => import("@/views/permissionManagement/operator/OperatorList"),
  'pms:operateLog:view': () => import("@/views/permissionManagement/operator/OperateLogList"),
  'pms:role:view': () => import('@/views/permissionManagement/role/RoleList'),
  'pms:function:view': () => import('@/views/permissionManagement/function/FunctionList'),
  'portal:function:view': () => import('@/views/portalPermissionManagement/function/index'),
  'portal:role:view': () => import('@/views/portalPermissionManagement/role/index'),
  'portal:operator:view': () => import('@/views/portalPermissionManagement/operator/index'),
  'config:dictionary:view': () => import('@/views/systemManagement/dataDictionary/DataDictionaryList'),
  'timer:schedule:view': () => import('@/views/systemManagement/timer/ScheduleJobList'),
  'message:trace:view': () => import('@/views/tradeManagement/messageTrace/MessageTraceList'),
  'account:sub:view': () => import('@/views/account/accountSub/AccountSubList'),
  'account:sub:processPending:view': () => import('@/views/account/accountSub/AccountSubProcessPendingList'),
  'account:sub:processDetail:view': () => import('@/views/account/accountSub/AccountSubProcessDetailList'),
  'account:sub:result:view': () => import('@/views/account/accountSub/AccountSubProcessResultList'),
  'config:globallock:view': () => import('@/views/systemManagement/lock/GlobalLockList'),
  'account:mch:view': () => import('@/views/account/accountMch/AccountMchList'),
  'account:mch:processPending:view': () => import('@/views/account/accountMch/AccountMchProcessPendingList'),
  'account:mch:processDetail:view': () => import('@/views/account/accountMch/AccountMchProcessDetailList'),
  'account:mch:result:view': () => import('@/views/account/accountMch/AccountMchProcessResultList'),

  'account:transit:view': () => import('@/views/account/accountTransit/AccountTransitList'),
  'account:transit:processPending:view': () => import('@/views/account/accountTransit/AccountTransitProcessPendingList'),
  'account:transit:processDetail:view': () => import('@/views/account/accountTransit/AccountTransitProcessDetailList'),
  'account:transit:result:view': () => import('@/views/account/accountTransit/AccountTransitProcessResultList'),

  'merchant:plat:view': () => import('@/views/merchant/MerchantList'),
};

function buildMenuTree(pid, pidGroup, resultArr) {
  let group = pidGroup[pid];
  if (!group) {
    return;
  }
  group.forEach((p) => {
    let menu = {}
    menu.meta = {title: p.name}
    menu.path = p.url;
    if (pidGroup[p.id]) {
      menu.children = [];
      buildMenuTree(p.id, pidGroup, menu.children)
    }
    resultArr.push(menu)
  })
}


export const buildMenu = function (userFunctions) {
  let pidGroup = {};
  userFunctions.filter(f => f.functionType === 1).forEach(f => {
    if (!pidGroup[f.parentId]) {
      pidGroup[f.parentId] = [f]
    } else {
      pidGroup[f.parentId].push(f)
    }
  });

  let resultArr = [];
  buildMenuTree(0, pidGroup, resultArr);
  return resultArr;
};


export const buildRoute = function (userFunctions) {
  let menuRoute = {path: '/', component: Layout, children: []};
  userFunctions.filter(f => f.functionType === 1).forEach(f => {
    menuRoute.children.push({
      component: routesInfo[f.permissionFlag],
      path: f.url,
      meta: {id: f.id, title: f.name, permissionFlag: f.permissionFlag}
    });
  });
  return [menuRoute];
};








