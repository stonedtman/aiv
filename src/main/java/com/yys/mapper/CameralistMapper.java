package com.yys.mapper;


import com.yys.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CameralistMapper {

    /**
     * 根据位置、状态等条件分页查询摄像头列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 摄像头列表
     */

    List<AiCamera> selectCameralist(@Param("gId") String gId,
                                    @Param("pageNum") int pageNum,
                                    @Param("pageSize") int pageSize);

    /**
     * 根据分组名称分页查询摄像头分组列表
     * @param groupName 分组名称
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 摄像头分组列表
     */
    List<CameraGroups> selectCameralistGroup(@Param("groupName") String groupName,
                                             @Param("pageNum") int pageNum,
                                             @Param("pageSize") int pageSize);

    /**
     * 查询所有摄像头分组
     * @return 分组列表
     */
    @Select("SELECT id,group_name FROM camera_sector")
    List<CameraSector> selectGroups ();

    @Select("SELECT COUNT(*) FROM camera_sector")
    int selectgroupCameralistCount();


    @Delete("delete from camera_sector where group_id=#{groupId}")
    int deleteCameraGroup(@Param("groupId")String groupId);

    @Delete("delete from ai_camera where camera_id=#{cameraId}")
    int deleteCameraList(@Param("cameraId")String cameraId);

    /**
     * 查询摄像头总数
     * @return 摄像头总数
     */

    int selectCameralistCount(String gId);

    @Select("SELECT * FROM ai_camera")
    List<AiCamera> selectAicameralist();

    @Select("SELECT COUNT(*) FROM ai_camera WHERE camera_status = 1")
    int selectWorkingCamera();

    @Update("UPDATE ai_camera SET camera_status = #{cameraStatus} WHERE camera_id = #{cameraId}")
    int updateCamerStats(@Param("cameraId") String cameraId,
                          @Param("cameraStatus") Integer cameraStatus);

    /**
     * 插入摄像头数量记录
     * @param cameraSector 摄像头分组信息
     * @return 影响的行数
     */
    int insertCameralistCount(CameraSector cameraSector);

    /**
     * 查询最新的分组ID
     * @return 最新分组ID
     */
    @Select("SELECT group_id FROM camera_sector  ORDER BY id DESC LIMIT 1")
    String selectGroupid();

    /**
     * 根据分组名称查询分组是否存在
     * @param groupName 分组名称
     * @return 分组是否存在（存在返回1，否则返回0）
     */
    @Select("SELECT COUNT(*) FROM camera_sector WHERE group_name = #{groupName}")
    int selectGroupExists(@Param("groupName") String groupName);

    /**
     * 插入摄像头信息
     * @param aiCamera 摄像头信息
     * @return 影响的行数
     */
    int insertCamera(AiCamera aiCamera);

    /**
     * 查询最新的摄像头ID
     * @return 最新摄像头ID
     */
    @Select("SELECT camera_id FROM ai_camera  ORDER BY id DESC LIMIT 1")
    String selectCameraid();

    /**
     * 根据位置查询摄像头是否存在
     * @param cameraLocation 摄像头位置
     * @return 摄像头是否存在（存在返回1，否则返回0）
     */
    @Select("SELECT COUNT(*) FROM ai_camera WHERE camera_location = #{cameraLocation}")
    int selectCameraExists(@Param("cameraLocation") String cameraLocation);

    /**
     * 根据位置查询摄像头在分组内是否存在
     * @param cameraLocation 摄像头位置
     * @return 摄像头是否存在（存在返回1，否则返回0）
     */
    int selectCameraGroupExists(@Param("cameraLocation") String cameraLocation,@Param("cameraGroup") String cameraGroup);

    /**
     * 根据视频流信息查询摄像头位置
     * @param videoStreaming 视频流信息
     * @return 摄像头位置
     */
    @Select("SELECT camera_location FROM ai_camera WHERE video_streaming = #{videoStreaming}")
    String selectCameralotation(@Param("videoStreaming") String videoStreaming);

    /**
     * 根据视频流信息查询摄像头位置
     * @param cameraId 视频流信息
     * @return 摄像头流
     */
    @Select("SELECT video_streaming FROM ai_camera WHERE camera_id = #{cameraId}")
    String selectCameraStream(@Param("cameraId") String cameraId);


    /**
     * 根据模型名称查询AI模型的名称
     *
     * @param model 模型名称
     * @return AI模型的名称
     */
    @Select("SELECT model_name FROM ai_model WHERE model = #{model}")
    String modelName(@Param("model") String model);

    /**
     * 更新摄像头分组信息
     * @param groupId 分组ID
     * @param groupName 分组名称
     *
     */
    @Update("UPDATE camera_sector SET group_name = #{groupName},update_time = #{updateTime} WHERE group_id = #{groupId}")
    int updateGroup(@Param("groupId") String groupId,@Param("groupName") String groupName,@Param("updateTime") String updateTime);

    /**
     * 查询摄像头分组名称
     * @param groupId 分组ID
     */
    GroupCamerasDTO selectGroupMsg(@Param("groupId") String groupId);

    //摄像机列表获取信息
    AiCamera selectCameraMsg(@Param("cameraId") String cameraId);

    //修改摄像机信息
    int updateCamera(AiCamera aiCamera);

    @Select("SELECT camera_location FROM ai_camera")
    List<String> selectCameraLocation();

    @Update("UPDATE ai_camera SET camera_img = #{cameraImg} WHERE camera_id = #{cameraId}")
    int updateCameraImg(@Param("cameraId") String cameraId,
                        @Param("cameraImg") String cameraImg);


    void insterModel(AiModels aiModels);


    @Update("UPDATE ai_model SET model_name = #{modelName} WHERE id = #{id}")
    int updateModelname(@Param("id") Integer id,
                        @Param("modelName") String modelName);
}
