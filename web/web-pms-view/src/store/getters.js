const getters = {
  sidebarCollapse: state => state.app.sidebarCollapse,
  device: state => state.app.device,
  userData: state => state.user.userData,
  visitedViews: state => state.tagsView.visitedViews,
}
export default getters
