package com.yys.entity;

import lombok.Data;

/**
 * 任务详情类
 */
@Data
public class DetailedTask {
    private String taskName;
    private String taskDescription;
    private String cameraLocation;
    private String videoStreaming;
    private String modelName;
    private String notificationEmail;
}
