package com.yys.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 目标检测任务类
 */
@Data
public class DetectionTask {
    private Integer id;
    @JsonProperty("ids")
    private String ids;
    private String taskId;
    private String taskName;
    private String cameraPosition;
    private Integer algorithmModel;
    private String taskDescription = "默认任务描述";
    private Integer priority;
    private String modelName;
    private String alertMethod;
    private String alertLevel;
    private String notificationEmail;
    private Integer targetCountThreshold;
    private Integer targetDwellTime;
    private String createTime;
    private String startTime;
    private String endTime;
    private Integer status;
    private String taskTagging;
    private String videoStreaming;
    private String groupName;
    List<AiModels> aiModels;
}
