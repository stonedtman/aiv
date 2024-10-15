package com.yys.mapper;

import com.yys.entity.DefaultaddressDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UntilMapper {

    List<DefaultaddressDTO> getaddress();


}
