package com.yys.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GetWarningSearchimg {
    String searchText;
    String startTime;
    String endTime;
    Integer pageNum;
    Integer pageSize;
    private MultipartFile image;
    List<String> alertTypes;
    List<String> cameraPosition;
}
