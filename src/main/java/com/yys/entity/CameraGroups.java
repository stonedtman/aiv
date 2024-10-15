package com.yys.entity;

import lombok.Data;

/**
 * 摄像头分组信息类
 */
@Data
public class CameraGroups {
    String groupId ;
    String groupName ;
    String videosums;
    String workingsums;
    String createTime ;
    String updateTime;
}
