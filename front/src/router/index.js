import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const originalPush = VueRouter.prototype.push;
// 解决ElementUI导航栏中的vue-router在3.0版本以上重复点菜单报错问题
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch((err) => err);
};

const routes = [
  {
    path: "/",
    redirect: "/login",
    component: () => import("../views/login.vue"),
  },
  {
    path: "/login",
    name: "login",
    component: () => import("../views/login.vue"),
  },
  {
    path: "/",
    name: "home",
    component: () => import("../views/home.vue"),
    children: [
      {
        path: "billboards",
        name: "billboards",
        component: () => import("../views/billboards/index.vue"),
      },
      {
        path: "taskMonitor",
        name: "taskMonitor",
        component: () => import("../views/taskMonitor/index.vue"),
      },
      {
        path: "eventWarning",
        name: "eventWarning",
        component: () => import("../views/eventWarning/index.vue"),
      },
      {
        path: "algorithmManage",
        name: "algorithmManage",
        component: () => import("../views/algorithmManage/index.vue"),
      },
      {
        path: "algorithmManage/tryout",
        name: "algorithmManageTryout",
        component: () => import("../views/algorithmManage/tryout.vue"),
      },
      {
        path: "videoAccess",
        name: "videoAccess",
        component: () => import("../views/videoAccess/index.vue"),
      },
      // {
      //   path: "videoRetrieve",
      //   name: "videoRetrieve",
      //   component: () => import("../views/videoRetrieve/index.vue"),
      // },
      {
        path: "systemSettings",
        name: "systemSettings",
        component: () => import("../views/systemSettings/index.vue"),
      },
      {
        path: "contact",
        name: "contact",
        component: () => import("../views/contact.vue"),
      },
    ],
  },
];

const router = new VueRouter({
  mode: "history",
  base: "/vis/",
  routes,
  // 当路由跳转后滚动条所在的位置
  scrollBehavior(to, from, savedPosition) {
    // return 期望滚动到哪个的位置
    return { x: 0, y: 0 };
  },
});

//路由前置守卫
router.beforeEach((to, from, next) => {
  if (!["/login"].includes(to.path)) {
    //判断进入其他页面是否携带token
    if (localStorage.getItem("Authorization")) {
      next();
    } else {
      router.replace("/login");
    }
  } else {
    next();
  }
});

export default router;
