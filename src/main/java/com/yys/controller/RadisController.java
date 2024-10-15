package com.yys.controller;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.Result;
import com.yys.service.RadisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/radis")
@CrossOrigin
public class RadisController {

    @Autowired
    private RadisService radisCounterService;


    /**
     * 处理请求以获取并返回消息
     * 该方法通过计算今日、昨日以及前日的计数来分析数据变化率
     *
     * @return 包含今日、昨日、前日计数以及各自相比前一天变化率的JSON字符串
     */
    @RequestMapping("/returnmsg")
    public String returnmsg(){
        Result result=getDataMsg();
        return JSON.toJSONString(result);
    }



    public Result getDataMsg() {
        // 获取今日计数
        int today=Math.toIntExact(radisCounterService.getTodayCount());
        // 获取昨日计数
        int yesterday=Math.toIntExact(radisCounterService.getYesterdayCount());
        // 获取前日计数
        int dayBeforeYesterday=Math.toIntExact(radisCounterService.getDayBeforeYesterdayCount());

        // 初始化今日相比昨日的变化率
        String rate1 = "";
        // 如果昨日计数不为0，计算变化率e
        if (yesterday != 0) {
            double num = ((double) today - yesterday) / yesterday;
            // 使用String.format方法格式化数字，保留两位小数
            rate1 = String.format("%.2f", num * 100);
        } else if (today == 0 && yesterday == 0) {
            // 如果昨日和前日计数都为0，变化率设为0%
            rate1 = "0.00";
        } else {
            // 其他情况下，变化率设为100%
            rate1 = "100.00";
        }

        // 初始化昨日相比前日的变化率
        String rate2 = "";
        // 如果前日计数不为0，计算变化率
        if (dayBeforeYesterday != 0) {
            double num = ((double) yesterday - dayBeforeYesterday) / dayBeforeYesterday;
            // 使用String.format方法格式化数字，保留两位小数
            rate2 = String.format("%.2f", num * 100);
        } else if (dayBeforeYesterday == 0 && yesterday == 0) {
            // 如果前日和昨日计数都为0，变化率设为0%
            rate2 = "0.00";
        } else {
            // 其他情况下，变化率设为100%
            rate2 = "100.00";
        }

        // 存储计数和变化率数据
        Map<String, Object> map = new HashMap<>();
        // 存储今日计数
        map.put("today", today);
        // 存储昨日计数
        map.put("yesterday", yesterday);
        // 存储今日相比昨日的变化率
        map.put("day-yesterday", rate1);
        // 存储昨日相比前日的变化率
        map.put("yesterday-before", rate2);
        // 返回包含结果的JSON字符串
        return Result.success("获取数据成功",1,map);
    }


    private String getDateTime(LocalDate date) {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(sdf);
    }



}