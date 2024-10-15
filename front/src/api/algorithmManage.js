import instance from "@/utils/intercept";

//应用场景
export function getSceneList() {
  return instance({
    url: "/plan/getModelTypes",
    method: "get",
  });
}

//算法列表
export function getAlgorithmList(data) {
  return instance({
    url: "/plan/getPlans",
    method: "get",
    params: data,
  });
}

//算法详情
export function getAlgorithDetail(data) {
  return instance({
    url: "/plan/getModelPlanbyid",
    method: "get",
    params: data,
  });
}

//模型导入
export function exportAlgorithModel(data) {
  return instance({
    url: "/plan/saveModelMsg",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    data,
  });
}
