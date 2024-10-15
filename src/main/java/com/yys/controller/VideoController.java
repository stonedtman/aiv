package com.yys.controller;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.Result;
import com.yys.entity.VideoStreaming;
import com.yys.service.RTSPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rtspvideo")
@CrossOrigin
public class VideoController {

    @Autowired
    private RTSPService rtspService;

    @PostMapping("/switchstream")
    public String streamVideo(@RequestBody VideoStreaming videoStreaming) {
        int i = rtspService.streamVideo(videoStreaming.getSessionId(), videoStreaming.getVideoStreaming());
        if (i == 1) {
            return JSON.toJSONString(Result.success("开启成功", 1, "URL已开始流式传输"));
        }
        return JSON.toJSONString(Result.success("开启失败", 0, "未开始传输，请检查摄像头状态"));
    }

    @PostMapping("/stop")
    public String stopStream(@RequestBody VideoStreaming videoStreaming) {
        rtspService.stopStreaming(videoStreaming.getSessionId());
        return JSON.toJSONString(Result.success("已停止", 1, "URL停止流式传输"));
    }

}


