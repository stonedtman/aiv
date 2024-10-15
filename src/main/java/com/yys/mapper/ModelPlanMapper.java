package com.yys.mapper;

import com.yys.entity.AiModelType;
import com.yys.entity.ModelPlan;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ModelPlanMapper {

    //算法商城列表信息
    List<ModelPlan> getMainMsgList(@Param("scene") String scene);

    //算法详情
    ModelPlan getModelPlan(@Param("Id") Integer Id);

    ModelPlan gettheModelPlan(@Param("Id") Integer Id);

    //应用场景
    @Select("SELECT * FROM ai_model_type")
    List<AiModelType> getModelTypes();

    int insertModelPlan(ModelPlan modelPlan);

    @Select("SELECT id FROM ai_model_type WHERE model_type = #{modelType}")
    Integer getmodeltypeid(@Param("modelType") String modelType);

    Integer insterTypes(@Param("modelType") String modelType);

    int deleteModelPlan(@Param("Id") Integer Id);
}
