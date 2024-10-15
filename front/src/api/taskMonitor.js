import instance from "@/utils/intercept";

//监测任务列表
export function getMonitorTask(data) {
  return instance({
    url: "/tasks/gettasklist",
    method: "get",
    params: data,
  });
}

//监测任务详情
export function getMonitorTaskDetail(data) {
  return instance({
    url: "/tasks/getDetectionTask",
    method: "get",
    params: data,
  });
}

//算法模型列表
export function getAllAlgorithm() {
  return instance({
    url: "/createdetectiontask/selectAimodels",
    method: "get",
  });
}

//监控点位列表
export function getMonitorDevice() {
  return instance({
    url: "/boards/location",
    method: "get",
  });
}

//添加预警任务
export function createAlarmTask(data) {
  return instance({
    url: "/createdetectiontask/insertDetectiontask",
    method: "post",
    data,
  });
}

//编辑预警任务
export function updateAlarmTask(data) {
  return instance({
    url: "/createdetectiontask/updateDetectiontask",
    method: "post",
    data,
  });
}

//启动预警任务
export function playAlarmTask(data) {
  return instance({
    url: "/createdetectiontask/startvideostream",
    method: "get",
    params: data,
  });
}

//停用预警任务
export function pauseAlarmTask(data) {
  return instance({
    url: "/createdetectiontask/stopvideostream",
    method: "get",
    params: data,
  });
}

//删除任务
export function deleteAlarmTask(data) {
  return instance({
    url: "/tasks/deletetask",
    method: "get",
    params: data,
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
