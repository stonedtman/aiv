package com.yys.entity;

import lombok.Data;

import java.util.List;

@Data
public class DefaultaddressDTO {
    private String cameraLocation;
    private List<AgreementDTO> agreements;
}
