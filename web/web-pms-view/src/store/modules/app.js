const state = {
    sidebarCollapse: false,
    device: 'desktop'
}

const mutations = {
    TOGGLE_SIDEBAR: (state, collapse) => {
        state.sidebarCollapse = collapse
    },
    TOGGLE_DEVICE: (state, device) => {
        state.device = device
    }
}

const actions = {
    toggleSideBar({commit}, collapse) {
        commit('TOGGLE_SIDEBAR', collapse)
    },
    toggleDevice({commit}, device) {
        commit('TOGGLE_DEVICE', device)
    }
}

export default {
    state,
    mutations,
    actions
}
