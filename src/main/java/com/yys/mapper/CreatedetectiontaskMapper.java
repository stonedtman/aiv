package com.yys.mapper;

import com.yys.entity.AiModels;
import com.yys.entity.DetectionTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface CreatedetectiontaskMapper {

    /**
     * 查询所有AI模型
     *
     * @return AI模型列表
     */
    List<AiModels> selectAimodels();

    /**
     * 插入新的检测任务
     *
     * @param detectionTask 检测任务对象，包含任务的详细信息
     * @return 插入操作影响的行数
     */
    int insertDetectiontask(DetectionTask detectionTask);

    /**
     * 根据模型ID查询AI模型
     *
     * @param ids 模型ID，用于定位特定的AI模型
     * @return 匹配的AI模型列表
     */
    List<AiModels> selectModel(@Param("ids") String ids);

    /**
     * 根据摄像头位置查询视频流地址
     *
     * @param cameraLocation 摄像头位置，用于确定视频流来源
     * @return 视频流地址
     */
    @Select("select video_streaming from ai_camera where camera_location = #{cameraLocation}")
    String selectvideostream(@Param("cameraLocation") String cameraLocation);

    /**
     * 查询最新的任务ID
     *
     * @return 最新的任务ID
     */
    @Select("SELECT task_id FROM detection_task  ORDER BY id DESC LIMIT 1")
    String selectdtid();

    @Select("SELECT id FROM detection_task WHERE task_id = #{taskId}")
    Integer selectId(@Param("taskId") String taskId);

    /**
     * 根据任务ID查询任务标记
     *
     * @param Id 任务ID，用于定位特定的检测任务
     * @return 任务标记
     */
    @Select("SELECT task_tagging FROM detection_task WHERE Id = #{Id}")
    String selecttaskTagging(@Param("Id") String Id);



    /**
     * 更新检测任务的状态，并清除任务标记
     * @param Id 任务ID，用于确定要更新的任务
     * @param status 任务的新状态
     * @return 更新操作影响的行数
     */
    @Update("UPDATE detection_task SET status = #{status},end_time = #{endTime},task_tagging = null WHERE id = #{Id}")
    int updateDetectiontask(@Param("Id") String Id, @Param("status") Integer status,@Param("endTime") String endTime);

    /**
     * 根据任务ID查询任务的摄像头位置和模型ID
     *
     * @param Id 任务ID，用于定位特定的检测任务
     * @return 包含任务位置和模型ID的检测任务对象
     */
    @Select("SELECT camera_position,ids FROM detection_task WHERE id = #{Id}")
    DetectionTask selectlocationids(@Param("Id") String Id);

    /**
     * 更新检测任务的标记、状态和开始时间
     *
     * @return 更新操作影响的行数
     */

    int updateDetectiontasktag(DetectionTask detectionTask);


    @Select("SELECT task_id FROM detection_task WHERE id = #{id}")
    String selectTaskId(@Param("id") String id);


    //修改检测任务
    int toupdateDetectiontask(DetectionTask detectionTask);


    AiModels selectModelById(@Param("id") Integer id);


    int deleteModelById(@Param("id") Integer id);



}

