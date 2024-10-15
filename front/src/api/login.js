import instance from "@/utils/intercept";

export function login(data) {
  return instance({
    url: "/user/login",
    method: "post",
    data,
  });
}

export function logout() {
  return instance({
    url: "/user/logout",
    method: "get",
  });
}

export function getUserInfo(data) {
  return instance({
    url: "/user/queryById/" + data.id,
    method: "get",
  });
}

export function changePassword(data) {
  return instance({
    url: "/user/changePassword",
    method: "get",
    params: data,
  });
}
