package com.yys.controller;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.AiModels;
import com.yys.entity.DetectionTask;
import com.yys.entity.Result;
import com.yys.service.DetectionTaskService;
import com.yys.service.StreamService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yys.service.CreatedetectiontaskService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/createdetectiontask")
@CrossOrigin
public class CreatedetectiontaskController {

    @Autowired
    private CreatedetectiontaskService createdetectiontaskService;

    @Autowired
    private DetectionTaskService detectionTaskService;

    @Autowired
    private StreamService streamService;

    @RequestMapping("/insertDetectiontask")
    public String insertDetectiontask(@RequestBody DetectionTask detectionTask) {

        if(detectionTask.getStatus() == 0){
            return waitstartDetectiontask(detectionTask);

        }else {
            return startDetectiontask(detectionTask);
        }
    }

    @RequestMapping("/updateDetectiontask")
    private String updateDetectiontask(@RequestBody DetectionTask detectionTask) {
        int i=detectionTaskService.selectDetectionTaskStatus(String.valueOf(detectionTask.getId()));
        if (i!=0){
            return JSON.toJSONString(Result.success(500,"任务未停止，请先停止任务再修改",1,null));
        }
        if(detectionTask.getStatus() == 0){
            return toupdateDetectiontask(detectionTask);
        }else {
            return waitstarUpdatetDetectiontask(detectionTask);
        }
    }

    private String waitstarUpdatetDetectiontask(DetectionTask detectionTask) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] rtspUrls= new String[1];
        rtspUrls[0] = createdetectiontaskService.selectvideostream(detectionTask.getCameraPosition());

        List<AiModels> list = createdetectiontaskService.selectModel(detectionTask.getIds());
        String[] modelPaths = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            modelPaths[i] = list.get(i).getModelLocation()+"/"+list.get(i).getModel();
        }

        String taskTagging = streamService.startStream(rtspUrls,modelPaths,detectionTask.getTaskId());

        // 解析JSON字符串
        JSONObject jsonObject = new JSONObject(taskTagging);


        String threadName = jsonObject.getString("thread_name");

        detectionTask.setTaskTagging(threadName);
        if (detectionTask.getTaskTagging() != null&&detectionTask.getTaskTagging().length()>0){
            detectionTask.setStatus(1);
            detectionTask.setStartTime(sdf.format(new Date()));
            int i= createdetectiontaskService.toupdateDetectiontask(detectionTask);
            if (i>0){
                return JSON.toJSONString(Result.success("修改任务成功",1,null));
            }
        }
        return JSON.toJSONString(Result.success("修改任务失败",0,null));
    }




    @RequestMapping("/selectAimodels")
    public String selectAimodels() {
        List<AiModels> list = createdetectiontaskService.selectAimodels();
        if (list != null) {
            return JSON.toJSONString(Result.success("获取成功",list.size(),list));
        }
        return JSON.toJSONString(Result.success("获取失败",0,list));
    }

    @RequestMapping("/startvideostream")
    public String starttvideostream(String Id) {
        DetectionTask detectionTask = createdetectiontaskService.selectlocationids(Id);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        detectionTask.setCreateTime(sdf.format(new Date()));

        String[] rtspUrls= new String[1];
        rtspUrls[0] = createdetectiontaskService.selectvideostream(detectionTask.getCameraPosition());

        List<AiModels> list = createdetectiontaskService.selectModel(detectionTask.getIds());
        String[] modelPaths = new String[list.size()];
        String modelName = "";
        for (int i = 0; i < list.size(); i++) {
            modelPaths[i] = list.get(i).getModelLocation()+"/"+list.get(i).getModel();
        }
        String taskId = createdetectiontaskService.selectTaskId(Id);

        String taskTagging = streamService.startStream(rtspUrls,modelPaths,taskId);

        // 解析JSON字符串
        JSONObject jsonObject = new JSONObject(taskTagging);


        String threadName = jsonObject.getString("thread_name");

        detectionTask.setTaskTagging(threadName);
        if (detectionTask.getTaskTagging() != null&&detectionTask.getTaskTagging().length()>0){
            detectionTask.setStatus(1);
            detectionTask.setStartTime(sdf.format(new Date()));
            detectionTask.setId(Integer.valueOf(Id));
            int i= createdetectiontaskService.updateDetectiontasktag(detectionTask);
            if (i>0){
                return JSON.toJSONString(Result.success("创建任务成功",1,null));
            }
        }
        return JSON.toJSONString(Result.success("创建任务失败",0,null));
    }


    @RequestMapping("/stopvideostream")
    public String stoptvideostream(String Id) {
        String taskTagging = createdetectiontaskService.selecttaskTagging(Id);

        streamService.stopStream(taskTagging);
        String endTime = getnowtime();
        int i=createdetectiontaskService.updateDetectiontask(Id,0,endTime);
        if (i>0){
            return JSON.toJSONString(Result.success("停止成功",1,null));
        }

        return JSON.toJSONString(Result.success("停止失败",0,null));
    }

    private String generateCameraId() {
        String oldId = createdetectiontaskService.selectdtid();
        if (oldId == null || oldId.isEmpty()) {
            return "task-001";
        }

        String numericPart = oldId.replaceAll("[^0-9]", "");
        if (numericPart.isEmpty()) {
            return "task-001";
        }
        int newNumericPart = Integer.parseInt(numericPart) + 1;
        return String.format("task-%03d", newNumericPart);
    }

    private String startDetectiontask(DetectionTask detectionTask) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        detectionTask.setCreateTime(sdf.format(new Date()));

        String[] rtspUrls = new String[1];
        rtspUrls[0] = createdetectiontaskService.selectvideostream(detectionTask.getCameraPosition());

        List<AiModels> list = createdetectiontaskService.selectModel(detectionTask.getIds());
        String[] modelPaths = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            modelPaths[i] = list.get(i).getModelLocation() + "/" + list.get(i).getModel();
        }

        // 生成 taskId 并插入检测任务
        detectionTask.setTaskId(generateCameraId());
        int insertResult = createdetectiontaskService.insertDetectiontask(detectionTask);
        if (insertResult <= 0) {
            return JSON.toJSONString(Result.success("创建任务失败", 0, null));
        }

        // 获取插入的检测任务的 ID
        String taskId = detectionTask.getTaskId();

        // 调用 streamService.startStream 传递 taskId
        String taskTagging = streamService.startStream(rtspUrls, modelPaths, taskId);

        // 解析 JSON 字符串
        JSONObject jsonObject = new JSONObject(taskTagging);

        // 提取 "thread_name" 字段的值
        String threadName = jsonObject.getString("thread_name");

        detectionTask.setTaskTagging(threadName);
        if (detectionTask.getTaskTagging() != null && detectionTask.getTaskTagging().length() > 0) {
            detectionTask.setStatus(detectionTask.getStatus());
            detectionTask.setStartTime(sdf.format(new Date()));
            detectionTask.setId(createdetectiontaskService.selectId(taskId));
            int updateResult = createdetectiontaskService.updateDetectiontasktag(detectionTask);
            if (updateResult > 0) {
                return JSON.toJSONString(Result.success("创建任务成功", 1, null));
            }
        }
        return JSON.toJSONString(Result.success("创建任务失败", 0, null));
    }


    private String waitstartDetectiontask(DetectionTask detectionTask){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        detectionTask.setCreateTime(sdf.format(new Date()));

        detectionTask.setStatus(detectionTask.getStatus());
        detectionTask.setTaskId(generateCameraId());
        int i= createdetectiontaskService.insertDetectiontask(detectionTask);
        if (i>0){
            return JSON.toJSONString(Result.success("创建任务成功",1,null));
        }
        return JSON.toJSONString(Result.success("任务插入失败,进程已停止",0,null));

    }

    private String getnowtime(){
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }

    //任务修改（未开启）
    private String toupdateDetectiontask(DetectionTask detectionTask){
        int i =createdetectiontaskService.toupdateDetectiontask(detectionTask);
        if (i>0){
            return JSON.toJSONString(Result.success("修改成功",1,null));
        }
        return JSON.toJSONString(Result.success("修改失败",1,null));
    }

}
