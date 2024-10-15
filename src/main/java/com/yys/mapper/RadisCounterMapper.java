package com.yys.mapper;

import org.apache.ibatis.annotations.Param;

public interface RadisCounterMapper {

    Integer insertDataSuns(@Param("date") String date,
                       @Param("sums") String sums);

    Integer updateDataSuns(@Param("date") String date,
                       @Param("sums") String sums);

    Integer selectDataSuns(@Param("date") String date);
}
