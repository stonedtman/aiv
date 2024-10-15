import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import echarts from "echarts";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
//修改element-ui组件库主题色
import "./assets/scss/element-variables.scss";
Vue.use(ElementUI);

import "./assets/iconfont/iconfont.css";

import VueApexCharts from "vue-apexcharts";
Vue.component("apexchart", VueApexCharts);

//图片放大缩小插件
import Viewer from "v-viewer";
import "viewerjs/dist/viewer.css";
Vue.use(Viewer, {
  //默认参数
  defaultOptions: {
    zIndex: 9999,
    debug: false,
    navbar: false,
    title: false,
    toolbar: {
      zoomIn: true,
      zoomOut: true,
      oneToOne: true,
      rotateRight: true,
      rotateLeft: true,
    },
  },
});

// 全屏插件
import fullscreen from "vue-fullscreen";
Vue.use(fullscreen);

//无限滚动插件
import VueSeamlessScroll from "vue-seamless-scroll";
Vue.component("vue-seamless-scroll", VueSeamlessScroll);

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: (h) => h(App),
}).$mount("#app");
