package com.yys.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class MessageData {

    @JSONField(name = "video_name")
    private String videoName;

    @JSONField(name = "image_name")
    private String imageName;

    @JSONField(name = "video_stream")
    private int videoStream;

    @JSONField(name = "creation_time")
    private String creationTime;

    @JSONField(name = "model_names")
    private List<String> modelNames;

    @JSONField(name = "stream_index")
    private int streamIndex;
}


