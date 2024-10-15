package com.yys.service.impl;


import com.yys.entity.*;
import com.yys.mapper.CameralistMapper;
import com.yys.mapper.DetectionTaskMapper;
import com.yys.service.DetectionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class DetectionTaskServiceimpl implements DetectionTaskService {


    // 自动注入DetectionTaskMapper接口的实现类，用于执行数据库操作
    @Autowired
    private DetectionTaskMapper detectionTaskMapper;

    @Autowired
    private CameralistMapper cameralistMapper;

    /**
     * 根据条件分页查询检测任务
     *
     * @param taskName 任务名称
     * @param alertLevel 告警级别
     * @param startTime 查询开始时间
     * @param endTime 查询结束时间
     * @param pageNum 当前页码
     * @param pageSize 每页显示的数量
     * @return 查询结果，包括成功与否的信息，总记录数和检测任务列表
     */
    public Result getDetectionTasks(String taskName, String alertLevel, LocalDateTime startTime, LocalDateTime endTime, int pageNum, int pageSize) {
        // 调用Mapper查询检测任务
        List<DetectionTask> detectionTasks = detectionTaskMapper.selectDetectionTasks(taskName, alertLevel, startTime, endTime,pageNum, pageSize);

        // 判断查询结果是否为空
        if (detectionTasks.size() == 0){
            // 如果为空，返回失败结果
            return Result.success("获取检测任务失败",0,detectionTasks);
        }
        int count = detectionTaskMapper.selectDetectionTaskCount();
        // 如果不为空，返回成功结果，包括总数和检测任务列表
        return Result.success("获取检测任务成功",count,detectionTasks);
    }

    /**
     * 根据任务ID获取任务详细信息
     *
     * @param taskId 任务ID
     * @return 查询结果，包括成功与否的信息，返回记录数和任务详细信息
     */
    @Override
    public Result getDetailedTask(String taskId) {
        // 调用Mapper查询任务详细信息
        DetectionTask detectionTask = detectionTaskMapper.selectDetailedTask(taskId);
        // 判断查询结果是否为空
        if (detectionTask != null){
            // 如果不为空，返回成功结果
            return Result.success("获取任务详情成功",1,detectionTask);
        }
        // 如果为空，返回失败结果
        return Result.success("获取任务详情失败",0,detectionTask);
    }

    @Override
    public Result deleteDetectionTask(String id) {
        int i=detectionTaskMapper.deleteDetectionTask(id);
        if (i>0){
            return Result.success("删除成功",1,null);
        }
        return Result.success("删除失败",0,null);
    }

    @Override
    public DetectionTask selectDetectionType(String taskId) {
        return detectionTaskMapper.selectDetectionType(taskId);
    }

    // 根据模型名称查询模型名称
    @Override
    public String modelName(String model) {
        return cameralistMapper.modelName(model);
    }

    @Override
    public Integer selectDetectionTaskStatus(String id) {
        return detectionTaskMapper.selectDetectionTaskStatus(id);
    }

    @Override
    public Result selectDetectiontask(String id) {
        DetectionTask detectiontask = detectionTaskMapper.selectDetectiontask(id);

        if (detectiontask != null){
            return Result.success("获取任务详情成功",1,detectiontask);
        }
        return Result.success("获取任务详情失败",0,null);
    }


}

