package com.yys.service;


import java.io.File;
import java.util.List;

public interface StreamService {

    // 启动视频流
    String startStream(String[] rtspUrls, String[] modelPaths,String taskId) ;

    // 停止视频流
    String stopStream(String name);

    int processModelFile(File file);

    //模型测试
    List<String> getimgmsg(String modelpath, File file);

    boolean deleteModel(String modelPath);


}


