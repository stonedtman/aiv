package com.yys.entity;

import lombok.Data;

@Data
public class AiUser {
    private String id;
    private String userName;
    private String userPwd;
    private String userEmial;
    private String permissions;
    private String token;
}
