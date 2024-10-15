package com.yys.service;


import com.yys.entity.*;

import java.util.List;


public interface CameralistService {
    int insertCamera(AiCamera aiCamera);
    int selectGroupExists(String groupName);
    int selectCameraExists(String cameraLocation);
    int selectCameraGroupExists(String cameraLocation,String cameraGroup);
    int insertCameralistCount(CameraSector cameraSector);
    int updateCamerStats(String cameraId, Integer cameraStatus);
    int selectWorkingCamera();
    int updateCameraImg(String cameraId, String cameraImg);
    int insterModel(AiModels aiModels);
    int updateModelname(Integer id, String modelName);

    Result selectGroups();
    Result deleteCameraGroup(String groupId);
    Result deleteCameraList(String cameraId);
    Result selectCameralistGroup(String groupName,int pageNum,int pageSize);
    Result selectCameralist(String gId,int pageNum,int pageSize);
    Result updateGroup(String groupId,String groupName);
    Result selectGroupMsg(String groupId);
    Result selectCameraMsg(String cameraId);
    Result updateCamera(AiCamera aiCamera);
    Result selectCameraLocation();

    String selectGroupid();
    String selectCameraid();
    String selectCameraStream(String cameraId);


    List<AiCamera> selectAicameralist();



}
