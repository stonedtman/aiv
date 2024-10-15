package com.yys.service.impl;

import com.yys.entity.AiModelType;
import com.yys.entity.ModelPlan;
import com.yys.entity.Result;
import com.yys.mapper.ModelPlanMapper;
import com.yys.service.ModelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelPlanServiceimpl implements ModelPlanService {

    @Autowired
    private ModelPlanMapper modelPlanMapper;

    @Override
    public Result getMainMsgList(String scene) {
        List<ModelPlan> list = modelPlanMapper.getMainMsgList(scene);

        if (list != null){
            return Result.success("获取列表成功",list.size(),list);
        }
        return Result.success("获取列表失败",0,new ArrayList<>());
    }

    @Override
    public Result getModelPlan(Integer Id) {
        ModelPlan modelPlan=modelPlanMapper.getModelPlan(Id);
        if (modelPlan != null){
            return Result.success("获取成功",1,modelPlan);
        }
        return Result.error("获取详细信息失败");
    }

    @Override
    public Result getModelTypes() {
        List<AiModelType> list=modelPlanMapper.getModelTypes();
        if (list != null){
            return Result.success("获取成功",list.size(),list);
        }
        return Result.error("获取失败");
    }

    @Override
    public ModelPlan gettheModelPlan(Integer Id) {

        ModelPlan modelPlan = modelPlanMapper.gettheModelPlan(Id);
        if (modelPlan != null){
            return modelPlan;
        }
        return null;
    }

    @Override
    public int deleteModelPlan(Integer Id) {

        if (modelPlanMapper.deleteModelPlan(Id) > 0){
            return 1;
        }
        return 0;
    }

    @Override
    public int insertModelPlan(ModelPlan modelPlan) {
        int i = modelPlanMapper.insertModelPlan(modelPlan);
        return i;
    }

    @Override
    public Integer getmodeltypeid(String modelType) {
        Integer modelTypeId = modelPlanMapper.getmodeltypeid(modelType);
        return modelTypeId;
    }

    @Override
    public Integer insterTypes(String modelType) {
        return modelPlanMapper.insterTypes(modelType);
    }
}
