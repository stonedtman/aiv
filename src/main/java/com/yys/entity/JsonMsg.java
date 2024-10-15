package com.yys.entity;

import lombok.Data;

import java.util.List;

@Data
public class JsonMsg {
    private String modelName;
    private List<String> modelTypes;
    private String modelExplain;
}
