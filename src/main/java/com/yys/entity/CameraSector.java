package com.yys.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CameraSector {
    private Integer id;
    private String groupId;
    private String groupName;
    private String createTime;
    private String updateTime;
}
