import instance from "@/utils/intercept";

//监控设备列表
export function getWarningEvent(data) {
  return instance({
    url: "/warningTable/selectwarning",
    method: "post",
    data,
  });
}

//预警类型列表
export function getAllAlgorithm() {
  return instance({
    url: "/createdetectiontask/selectAimodels",
    method: "get",
  });
}

//摄像头点位
export function getAllLocations() {
  return instance({
    url: "/sterams/selectCameraLocation",
    method: "get",
  });
}

//预警信息详情
export function getWarningEventDetail(data) {
  return instance({
    url: "/warningTable/selectbytaskid",
    method: "get",
    params: data,
  });
}
