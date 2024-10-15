package com.yys.entity;

import lombok.Data;

import java.util.List;

@Data
public class GroupCamerasDTO {
    String id ;
    String groupId ;
    String groupName ;
    String videosums;
    String workingsums;
    String createTime ;
    String updateTime;;
    private List<CameraDTO> cameras;
}
