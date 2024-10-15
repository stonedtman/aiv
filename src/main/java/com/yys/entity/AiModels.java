package com.yys.entity;

import lombok.Data;

@Data
public class AiModels {
    private Integer id;
    private String model;
    private String modelName;
    private Integer modelSource;
    private String createTime;
    private String updateTime;
    private String modelLocation;
    private String modelVersion;
}
