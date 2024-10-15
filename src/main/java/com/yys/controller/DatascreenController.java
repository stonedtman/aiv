package com.yys.controller;

import com.alibaba.fastjson2.JSON;

import com.yys.entity.Result;
import com.yys.service.DatascreenService;
import com.yys.service.RTSPService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/boards")
@CrossOrigin
public class DatascreenController {

    @Resource
    private DatascreenService datascreenService;

    @Resource
    private RTSPService rtspservice;

    /**
     * 查询摄像头列表分组
     *
     * @return 返回一个包含摄像头分组信息的列表
     */
    @RequestMapping("/location")
    public String getCameralistGroup() {
        Result result=datascreenService.selectCameralistGroup();
        return JSON.toJSONString(result);
    }

}
