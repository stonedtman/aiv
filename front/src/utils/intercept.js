import axios from "axios";
//引入路由
import router from "@/router";
// 使用element-ui Message做消息提醒
import { Message } from "element-ui";
//引入全局请求地址变量
import baseURL from "./request";

//创建axios实例
const instance = axios.create({
  baseURL: baseURL, //请求的地址
  timeout: 1000 * 60, //超时请求时间
});

// 请求拦截器
instance.interceptors.request.use(
  (request) => {
    //配置白名单 请求不携带token
    var whiteList = ["/user/login"];
    if (!whiteList.includes(request.url)) {
      if (localStorage.getItem("Authorization")) {
        request.headers.Authorization =
          "Bearer " + localStorage.getItem("Authorization");
      }
    }

    return request;
  },
  (error) => Promise.reject(error)
);

// 响应连接器
instance.interceptors.response.use(
  (response) => {
    // console.log(typeof response, "response");
    if (response.data.code && response.data.code !== 200) {
      Message.error(response.data.msg);
    }
    return Promise.resolve(response.data);
  },
  (error) => {
    console.log(error, "error");
    if (error && error.response) {
      if (error.code === "ECONNABORTED") {
        Message.error("请求超时");
      }

      var blackList = ["/rtspvideo/stop"];
      // 登录失效返回登录页
      if (error.response.data.code == 401) {
        //自动退出 页面销毁会调用该接口 登录过期信息不需要弹出
        if (!blackList.includes(error.response.config.url)) {
          Message.error(error.response.data.msg);
        }
        localStorage.removeItem("Authorization");
        localStorage.removeItem("permissions");
        router.replace({ path: "/login" });
      } else {
        Message.error(error.message);
      }
    }
    return Promise.resolve(error.response);
  }
);

export default instance;
