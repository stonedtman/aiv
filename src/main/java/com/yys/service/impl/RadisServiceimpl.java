package com.yys.service.impl;

import com.yys.mapper.RadisCounterMapper;
import com.yys.service.RadisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

// Redis计数器服务实现类
@Service
public class RadisServiceimpl implements RadisService {

    // 定义日期格式化器，用于生成Redis键名中的日期部分
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 注入Redis模板，用于操作Redis数据库
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RadisCounterMapper redisCounterMapper;

    /**
     * 增加计数器的值
     * 每天对应一个唯一的Redis键，此方法用于增加当天的计数
     */
    @Override
    public void incrementCounter() {
        // 获取当前日期对应的Redis键名
        String todayKey = getKeyForDate(LocalDate.now());
        // 增加当天计数器的值
        redisTemplate.opsForValue().increment(todayKey);
        // 设置过期时间为3天，以确保计数器不会无限增长
        redisTemplate.expire(todayKey, 4, TimeUnit.DAYS);
    }

    /**
     * 获取当天的计数
     *
     * @return 当天的计数
     */
    @Override
    public Long getTodayCount() {
        String todayKey = getKeyForDate(LocalDate.now());
        return getCount(todayKey);
    }

    /**
     * 获取昨天的计数
     *
     * @return 昨天的计数
     */
    @Override
    public Long getYesterdayCount() {
        String yesterdayKey = getKeyForDate(LocalDate.now().minusDays(1));
        return getCount(yesterdayKey);
    }

    /**
     * 获取前天的计数
     *
     * @return 前天的计数
     */
    @Override
    public Long getDayBeforeYesterdayCount() {
        String dayBeforeYesterdayKey = getKeyForDate(LocalDate.now().minusDays(2));
        return getCount(dayBeforeYesterdayKey);
    }

    @Override
    public Integer insertDataSuns(String date, String sums) {
        return redisCounterMapper.insertDataSuns(date, sums);
    }

    @Override
    public Integer updateDataSuns(String date, String sums) {
        return redisCounterMapper.updateDataSuns(date, sums);
    }

    @Override
    public Integer selectDataSuns(String date) {
        return redisCounterMapper.selectDataSuns(date);
    }

    @Override
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public String setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value, 20, TimeUnit.MINUTES);
        return "保存成功";
    }

    /**
     * 根据日期生成Redis键名
     *
     * @param date 需要生成键名的日期
     * @return 生成的Redis键名
     */
    private String getKeyForDate(LocalDate date) {
        return "counter:" + date.format(FORMATTER);
    }

    /**
     * 获取指定键的计数
     * 如果键不存在或值不是数字，将返回0
     *
     * @param key Redis键名
     * @return 键对应的计数
     */
    private Long getCount(String key) {
        String count = redisTemplate.opsForValue().get(key);
        return count != null ? Long.parseLong(count) : 0L;
    }
}

