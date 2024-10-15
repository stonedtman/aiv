package com.yys.controller;


import com.alibaba.fastjson2.JSON;

import com.yys.entity.Result;
import com.yys.service.DetectionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/tasks")
@CrossOrigin
public class DetectionTaskController {

    @Autowired
    private DetectionTaskService detectionTaskService;

    @GetMapping("/gettasklist")
    public String getDetectionTasks(
            @RequestParam(value = "taskName", required = false) String taskName,
            @RequestParam(value = "alertLevel", required = false) String alertLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        if ("全部".equals(alertLevel)) {
            alertLevel = null;
        }
        pageNum=(pageNum-1)*pageSize;

        Result result = detectionTaskService.getDetectionTasks(taskName, alertLevel, startTime, endTime, pageNum,pageSize);
        return JSON.toJSONString(result);
    }

    @GetMapping("/taskdetailed")
    public String getDetailedTask(@RequestParam(value = "taskId", required = false) String taskId) {
        Result result = detectionTaskService.getDetailedTask(taskId);
        return JSON.toJSONString(result);
    }

    @GetMapping("/deletetask")
    public String deleteDetectionTask(@RequestParam(value = "Id", required = false) String Id) {
        Integer i = detectionTaskService.selectDetectionTaskStatus(Id);
        if (i == 1) {
            return JSON.toJSONString(Result.success(500,"该任务正在运行，无法删除！",0,null));
        }
        Result result = detectionTaskService.deleteDetectionTask(Id);
        return JSON.toJSONString(result);
    }

    @GetMapping("/getDetectionTask")
    public String getDetectionTask(String Id){
        return JSON.toJSONString(detectionTaskService.selectDetectiontask(Id));
    }
}

