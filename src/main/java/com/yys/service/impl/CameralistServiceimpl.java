package com.yys.service.impl;


import com.yys.entity.*;
import com.yys.mapper.CameralistMapper;
import com.yys.service.CameralistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class CameralistServiceimpl implements CameralistService {


    // 自动注入摄像头映射器，用于执行数据库操作
    @Autowired
    private CameralistMapper cameralistMapper;

    @Override
    public Result deleteCameraGroup(String groupId) {
        int i=cameralistMapper.deleteCameraGroup(groupId);
        if (i>0){
            return Result.success("删除成功",1,null);
        }
        return Result.success("删除失败",0,null);
    }

    @Override
    public Result deleteCameraList(String cameraId) {
        int i = cameralistMapper.deleteCameraList(cameraId);
        if (i>0){
            return Result.success("删除成功",1,null);
        }
        return Result.success("删除失败",0,null);
    }

    // 根据摄像头位置、状态等条件分页查询摄像头列表
    @Override
    public Result selectCameralist(String gId,int pageNum,int pageSize) {
        List<AiCamera> aiCameraList = cameralistMapper.selectCameralist(gId, pageNum, pageSize);
        int count = cameralistMapper.selectCameralistCount(gId);
        if (aiCameraList != null) {
            return Result.success("获取摄像头列表成功", count, aiCameraList);
        }
        return Result.success("获取摄像头列表失败",0, aiCameraList);
    }

    /**
     * 更新相机分组信息
     *
     * @param groupId 分组ID，用于定位要更新的分组
     * @param groupName 新的分组名称，用于更新分组的名称
     * @return 返回更新操作的结果，成功或失败
     *
     * 此方法通过调用cameralistMapper的updateGroup方法来更新分组信息，
     * 如果更新成功（影响行数大于0），则返回更新成功的结果，否则返回更新失败的结果
     */
    @Override
    public Result updateGroup(String groupId,String groupName) {
        String updateTime = getnowtime();
        int i = cameralistMapper.updateGroup(groupId, groupName,updateTime);
        if (i>0){
            return Result.success("更新成功",1,null);
        }
        return Result.success("更新失败",0,null);
    }

    /**
     * 查询指定分组的详细信息
     *
     * @param groupId 要查询的分组ID，用于定位特定分组
     * @return 返回查询操作的结果，包括分组信息或状态码和消息
     *
     * 此方法通过调用cameralistMapper的selectGroupMsg方法来获取指定分组的信息，
     * 如果获取成功（cameraMsg不为null），则返回获取成功的状态和分组信息，否则返回获取失败的状态
     */
    @Override
    public Result selectGroupMsg(String groupId) {
        GroupCamerasDTO cameraMsg = cameralistMapper.selectGroupMsg(groupId);
        if(cameraMsg == null){
            return Result.success("获取失败",0,null);
        }
        return Result.success("获取成功", 1, cameraMsg);
    }


    // 根据分组名称分页查询摄像头分组列表
    @Override
    public Result selectCameralistGroup(String groupName,int pageNum,int pageSize) {
        List<CameraGroups> cameraMsgList = cameralistMapper.selectCameralistGroup(groupName, pageNum, pageSize);
        int count = cameralistMapper.selectgroupCameralistCount();
        if (cameraMsgList != null) {
            return Result.success("获取摄像头分组列表成功",count, cameraMsgList);
        }
        return Result.success("获取摄像头分组列表失败",0, cameraMsgList);
    }

    // 查询所有摄像头分组
    @Override
    public Result selectGroups() {
        List<CameraSector> cameraMsgList = cameralistMapper.selectGroups();
        if (cameraMsgList.size() == 0){
            return Result.success("获取摄像头分组列表失败",0, cameraMsgList);
        }
        return Result.success("获取摄像头分组列表成功",cameraMsgList.size(), cameraMsgList);
    }

    @Override
    public List<AiCamera> selectAicameralist() {
        return cameralistMapper.selectAicameralist();
    }

    // 插入一个新的摄像头分组，并返回影响的行数
    @Override
    public int insertCameralistCount(CameraSector cameraSector) {
        int i = cameralistMapper.insertCameralistCount(cameraSector);
        return i;
    }

    @Override
    public int updateCamerStats(String cameraId, Integer cameraStatus) {
        return cameralistMapper.updateCamerStats(cameraId, cameraStatus);
    }

    @Override
    public int selectWorkingCamera() {
        return cameralistMapper.selectWorkingCamera();
    }

    @Override
    public int updateCameraImg(String cameraId, String cameraImg) {
        return cameralistMapper.updateCameraImg(cameraId, cameraImg);
    }

    @Override
    public int insterModel(AiModels aiModels) {
        cameralistMapper.insterModel(aiModels);
        return aiModels.getId();
    }

    @Override
    public int updateModelname(Integer id, String modelName) {
        int i=cameralistMapper.updateModelname(id, modelName);
        return i;
    }

    // 查询可分配的分组ID
    @Override
    public String selectGroupid() {
        return cameralistMapper.selectGroupid();
    }

    // 检查分组名称是否已存在，返回存在的数量
    @Override
    public int selectGroupExists(String groupName) {
        int i = cameralistMapper.selectGroupExists(groupName);
        return i;
    }

    // 插入一个新的摄像头，返回影响的行数
    @Override
    public int insertCamera(AiCamera aiCamera) {
        int i = cameralistMapper.insertCamera(aiCamera);
        return 0;
    }

    // 查询可分配的摄像头ID
    @Override
    public String selectCameraid() {
        return cameralistMapper.selectCameraid();
    }

    @Override
    public String selectCameraStream(String cameraId) {
        return cameralistMapper.selectCameraStream(cameraId);
    }

    //获取摄像头信息
    @Override
    public Result selectCameraMsg(String cameraId) {

        AiCamera aiCamera = cameralistMapper.selectCameraMsg(cameraId);
        if (aiCamera == null){
            return Result.success("获取摄像头信息失败",0,null);
        }
        return Result.success("获取摄像头信息成功",1,aiCamera);
    }

    @Override
    public Result updateCamera(AiCamera aiCamera) {
        int i= cameralistMapper.updateCamera(aiCamera);
        if (i>0){
            return Result.success("更新成功",1,null);
        }
        return Result.success("更新失败",0,null);
    }

    @Override
    public Result selectCameraLocation() {
        List<String> list = cameralistMapper.selectCameraLocation();
        if (list != null){
            return Result.success("获取摄像头位置成功",list.size(),list);
        }
        return Result.success("获取摄像头位置失败",0,list);
    }

    // 检查摄像头位置是否已存在，返回存在的数量
    @Override
    public int selectCameraExists(String cameraLocation) {
        return cameralistMapper.selectCameraExists(cameraLocation);
    }

    @Override
    public int selectCameraGroupExists(String cameraLocation, String cameraGroup) {
        return cameralistMapper.selectCameraGroupExists(cameraLocation,cameraGroup);
    }

    private String getnowtime(){
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }
}
