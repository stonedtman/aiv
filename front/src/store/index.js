import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    token: "",
    userId: "",
  },
  mutations: {
    setToken(state, token) {
      state.token = token;
    },
    setUserId(state, userId) {
      state.userId = userId;
    },
    clearAuth(state) {
      state.token = "";
      state.userId = "";
    },
  },
  actions: {},
  modules: {},
});
