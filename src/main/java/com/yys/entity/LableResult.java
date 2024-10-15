package com.yys.entity;

import lombok.Data;

import java.util.List;

@Data
public class LableResult {
    private Integer id;
    private Integer width;
    private Integer height;
    private List<Labels> labels;
    private String tagnames;
}
