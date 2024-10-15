import instance from "@/utils/intercept";

//摄像头列表
export function getVideoDevice() {
  return instance({
    url: "/boards/location",
    method: "get",
  });
}

//摄像头视频画面列表
export function getVideoList(data) {
  return instance({
    url: "/sterams/getvideolist",
    method: "get",
    params: data,
  });
}

//摄像头详情信息
export function getVideoDeviceDetail(data) {
  return instance({
    url: "/sterams/selectCameraMsg",
    method: "get",
    params: data,
  });
}

//添加摄像头
export function createVideoDevice(data) {
  return instance({
    url: "/sterams/addCamera",
    method: "post",
    data,
  });
}

//删除摄像头
export function deleteVideoDevice(data) {
  return instance({
    url: "/sterams/deleteCameraList",
    method: "get",
    params: data,
  });
}

//编辑添加摄像头
export function updateVideoDevice(data) {
  return instance({
    url: "/sterams/updateCamera",
    method: "post",
    data,
  });
}

//摄像头分组(不分页)
export function getAllVideoDeviceGroup() {
  return instance({
    url: "/sterams/getgroups",
    method: "get",
  });
}

//摄像头分组列表
export function getVideoDeviceGroup(data) {
  return instance({
    url: "/sterams/getvideolistgroup",
    method: "get",
    params: data,
  });
}

//添加摄像头分组
export function createVideoDeviceGroup(data) {
  return instance({
    url: "/sterams/addgroups",
    method: "get",
    params: data,
  });
}

//编辑摄像头分组
export function updateVideoDeviceGroup(data) {
  return instance({
    url: "/sterams/updateGroup",
    method: "get",
    params: data,
  });
}

//删除摄像头分组
export function deleteVideoDeviceGroup(data) {
  return instance({
    url: "/sterams/deleteCameraGroup",
    method: "get",
    params: data,
  });
}

//摄像头分组详情
export function getVideoDeviceGroupDetail(data) {
  return instance({
    url: "/sterams/getGroupMsg",
    method: "get",
    params: data,
  });
}

//获取监控设备协议类型
export function getDeviceProtocol(data) {
  return instance({
    url: "/until/getadress",
    method: "get",
  });
}

//开启视频流传输
export function enabledStreamTransmit(data) {
  return instance({
    url: "/rtspvideo/switchstream",
    method: "post",
    data,
  });
}

//断开视频流传输
export function disabledStreamTransmit(data) {
  return instance({
    url: "/rtspvideo/stop",
    method: "post",
    data,
  });
}
