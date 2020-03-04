const state = {
  userData: undefined,
}

const mutations = {
  SET_USER_DATA: (state, userData) => {
    state.userData = userData
  },
}

const actions = {

  setUserData({commit}, userData) {
    return new Promise(resolve => {
      commit('SET_USER_DATA', userData)
      resolve(userData)
    })
  },
}

export default {
  state,
  mutations,
  actions
}
