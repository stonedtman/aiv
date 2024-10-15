import instance from "@/utils/intercept";

//视频列表
export function getVideoList(data) {
  return instance({
    url: "/warningTable/selectwarning",
    method: "post",
    data,
  });
}

//点位列表
export function getLocationList() {
  return instance({
    url: "/sterams/selectCameraLocation",
    method: "get",
  });
}

//预警类型
export function getAllAlgorithm() {
  return instance({
    url: "/createdetectiontask/selectAimodels",
    method: "get",
  });
}

//图搜视频
export function getVideoListByImage(data) {
  return instance({
    url: "/warningTable/selectwarningbyimg",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    data,
  });
}

//视频详情
export function getVideoDetail(data) {
  return instance({
    url: "/warningTable/selectbytaskid",
    method: "get",
    params: data,
  });
}
