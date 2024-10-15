package com.yys.entity;

import lombok.Data;

import java.util.List;

@Data
public class RabbitMsg {
    private String videoPath;
    private String imgPath;
    private String rtspUrl;
    private String time;
    private List<String> model;
    private String taskId;
    private Integer index;
}

