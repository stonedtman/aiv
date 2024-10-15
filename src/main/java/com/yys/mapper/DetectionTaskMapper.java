package com.yys.mapper;


import com.yys.entity.DetailedTask;
import com.yys.entity.DetectionTask;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
public interface DetectionTaskMapper {
    List<DetectionTask> selectDetectionTasks(@Param("taskName") String taskName,
                                             @Param("alertLevel") String alertLevel,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("pageNum") int pageNum,
                                             @Param("pageSize") int pageSize);

    DetectionTask selectDetailedTask(@Param("taskId") String taskId);

    @Select("SELECT COUNT(*) FROM detection_task")
    int selectDetectionTaskCount();

    @Delete("DELETE FROM detection_task WHERE id = #{id}")
    int deleteDetectionTask(@Param("id") String id);

    @Select("SELECT status FROM detection_task WHERE id = #{id}")
    Integer selectDetectionTaskStatus(@Param("id") String id);

    @Select("SELECT * FROM detection_task WHERE task_id = #{taskId}")
    DetectionTask selectDetectionType(@Param("taskId") String taskId);

    //检测任务详情
    DetectionTask selectDetectiontask(@Param("id") String id);
}


