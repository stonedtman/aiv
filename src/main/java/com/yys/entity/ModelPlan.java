package com.yys.entity;

import lombok.Data;

import java.util.List;

@Data
public class ModelPlan {
    private String id;
    private String modelId;
    private String modelName;
    private String modelExplain;
    private String imgs;
    private String imgDetail;
    private String imgTest;
    private String testResult;
    private String scene;
    private List<String> modelType;
    private AiModels aiModels;
}
