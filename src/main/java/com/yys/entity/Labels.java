package com.yys.entity;

import lombok.Data;

@Data
public class Labels {

    private String zhLabel;
    private String enLabel;
    private String bgColor;
    private double x;
    private double y;
    private double width;
    private double height;
    private double confidence;

}
