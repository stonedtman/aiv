package com.yys.service.impl;


import com.yys.entity.CameraGroups;
import com.yys.entity.Result;
import com.yys.mapper.DatascreenMapper;
import com.yys.service.DatascreenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatascreenServiceimpl implements DatascreenService {

    @Autowired
    private DatascreenMapper datascreenMapper;
    @Override
    public Result selectCameralistGroup() {
        List<CameraGroups> list = datascreenMapper.selectCameralistGroup();
        if (list != null){
            return Result.success("获取列表成功", list.size(),list);
        }
        return Result.success("获取列表失败", 0,null);
    }
}
