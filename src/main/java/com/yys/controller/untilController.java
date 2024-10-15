package com.yys.controller;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.Result;
import com.yys.service.UntilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/until")
@CrossOrigin
public class untilController {

    @Autowired
    private UntilService untilService;

    @RequestMapping("/getadress")
    public String getadress(){
        Result result =untilService.getaddress();

        return JSON.toJSONString(result);

    }
}
