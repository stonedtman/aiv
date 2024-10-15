package com.yys.mapper;


import com.yys.entity.CameraGroups;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DatascreenMapper {


    /**
     * 查询摄像头列表分组
     *
     * @return 返回一个包含摄像头分组信息的列表
     */
    List<CameraGroups> selectCameralistGroup();

    @Select("SELECT video_streaming FROM ai_camera WHERE camera_location =  #{cameraLocation}")
    String selectCameVideostream(@Param("cameraLocation") String cameraLocation);
}
