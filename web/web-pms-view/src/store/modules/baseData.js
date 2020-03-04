import request from '@/utils/request'

const state = {
  // dictionary: JSON.parse(sessionStorage.getItem('dictionary')) || undefined,
  dictionary: undefined
}

const mutations = {
  SET_DICTIONARY: (state, dictionary) => {
    state.dictionary = dictionary
    sessionStorage.setItem("dictionary", JSON.stringify(dictionary))
  }
}

const actions = {
  //刷新数据字典
  refreshDictionary({commit}) {
    return new Promise((resolve, reject) => {
      request.get("/dataDictionary/listAllDataDictionaryVO").then(({data}) => {
        let dictionary = {}
        data.forEach(p => {
          dictionary[p.dataName] = p.dataInfo;
        })
        commit('SET_DICTIONARY', dictionary)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  }
}

export default {
  state,
  mutations,
  actions
}
