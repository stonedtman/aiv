package com.yys.service.impl;

import com.yys.entity.AiModels;
import com.yys.entity.DetectionTask;
import com.yys.mapper.CreatedetectiontaskMapper;
import com.yys.service.CreatedetectiontaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CreatedetectiontaskServiceimpl implements CreatedetectiontaskService {

    @Autowired
    private CreatedetectiontaskMapper createdetectiontaskMapper;
    @Override
    public List<AiModels> selectAimodels() {
        return createdetectiontaskMapper.selectAimodels();
    }

    @Override
    public int insertDetectiontask(DetectionTask detectionTask) {
        return createdetectiontaskMapper.insertDetectiontask(detectionTask);
    }

    @Override
    public String selectvideostream(String cameraLocation) {
        return createdetectiontaskMapper.selectvideostream(cameraLocation);
    }

    @Override
    public List<AiModels> selectModel(String ids) {
        return createdetectiontaskMapper.selectModel(ids);
    }

    @Override
    public String selectdtid() {
        return createdetectiontaskMapper.selectdtid();
    }

    @Override
    public String selecttaskTagging(String Id) {
        return createdetectiontaskMapper.selecttaskTagging(Id);
    }

    @Override
    public int updateDetectiontask(String Id, Integer status,String endTime) {
        return createdetectiontaskMapper.updateDetectiontask(Id,status,endTime);
    }

    @Override
    public DetectionTask selectlocationids(String Id) {
        return createdetectiontaskMapper.selectlocationids(Id);
    }

    @Override
    public int updateDetectiontasktag(DetectionTask detectionTask) {
        return createdetectiontaskMapper.updateDetectiontasktag(detectionTask);
    }

    @Override
    public String selectTaskId(String Id) {
        return createdetectiontaskMapper.selectTaskId(Id);
    }

    @Override
    public Integer selectId(String taskId) {
        return createdetectiontaskMapper.selectId(taskId);
    }

    @Override
    public int toupdateDetectiontask(DetectionTask detectionTask) {
        return createdetectiontaskMapper.toupdateDetectiontask(detectionTask);
    }

    @Override
    public AiModels selectModelById(Integer id) {
        return createdetectiontaskMapper.selectModelById(id);
    }

    @Override
    public int deleteModelPlan(Integer id) {
        if (createdetectiontaskMapper.deleteModelById(id) > 0){
            return 1;
        }
        return 0;
    }


}
