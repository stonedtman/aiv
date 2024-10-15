package com.yys.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class GetWarningSearch {
    String searchText;
    String startTime;
    String endTime;
    Integer pageNum;
    Integer pageSize;
    List<String> alertTypes;
    List<String> cameraPosition;
}
