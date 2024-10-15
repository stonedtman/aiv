package com.yys.service;


import com.yys.entity.ModelPlan;
import com.yys.entity.Result;
import org.apache.ibatis.annotations.Param;

public interface ModelPlanService {
    Result getMainMsgList(String scene);
    Result getModelPlan(Integer Id);
    Result getModelTypes();

    ModelPlan gettheModelPlan(Integer Id);


    int deleteModelPlan(Integer Id);
    int insertModelPlan(ModelPlan modelPlan);
    Integer getmodeltypeid(String modelType);
    Integer insterTypes(String modelType);


}
