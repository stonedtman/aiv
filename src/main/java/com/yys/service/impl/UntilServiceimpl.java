package com.yys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.DefaultaddressDTO;
import com.yys.entity.Result;
import com.yys.mapper.UntilMapper;
import com.yys.service.UntilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UntilServiceimpl implements UntilService {

    @Autowired
    private UntilMapper untilMapper;


    @Override
    public Result getaddress() {
        List<DefaultaddressDTO> list = untilMapper.getaddress();
        if (list.size() == 0){
            return Result.success("获取失败",0,null);
        }
        return Result.success("获取成功",list.size(),list);
    }
}
