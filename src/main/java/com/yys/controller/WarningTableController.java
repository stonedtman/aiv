package com.yys.controller;


import com.alibaba.fastjson2.JSON;
import com.yys.entity.GetWarningSearch;
import com.yys.entity.GetWarningSearchimg;
import com.yys.entity.Result;
import com.yys.entity.WarningTable;
import com.yys.service.RadisService;
import com.yys.service.WarningTableService;
import com.yys.util.ImageClassificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/warningTable")
@CrossOrigin
public class WarningTableController {

    @Autowired
    private WarningTableService warningTableService;

    @Autowired
    private ImageClassificationUtil imageClassificationUtil;

    @Autowired
    private RadisService radisService;

    @GetMapping("/getwarning")
    public String searchWithSort() {
        List<WarningTable> list= warningTableService.searchWithSort();
        Result result = Result.success(list);
        if (list.size() > 0) {
            return JSON.toJSONString(Result.success("获取成功", list.size(), list));
        }
        return JSON.toJSONString(Result.success("获取失败", 0, list));
    }

    @GetMapping("/selectbytaskid")
    private String selectbytaskid(String alertId){
        WarningTable warningTable = warningTableService.searchByAlertId(alertId);
        if (warningTable != null){
            return JSON.toJSONString(Result.success("获取成功", 1, warningTable));
        }
        return JSON.toJSONString(Result.success("获取失败", 0, warningTable));
    }
    @PostMapping("/selectwarningbyimg")
    public String searchWarnings(@ModelAttribute GetWarningSearchimg getWarningSearchimg) throws IOException {

        if (!getWarningSearchimg.getImage().isEmpty()){
            MultipartFile file = getWarningSearchimg.getImage();
            byte[] bytes = file.getBytes();
            // 获取文件名作为 Redis 的 key
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isEmpty()) {
                throw new IllegalArgumentException("文件名不能为空");
            }
            // 使用文件名作为 key 检查 Redis 中是否存在
            String imgTags = radisService.getValue(fileName);
            if (imgTags == null) {
                // 如果 Redis 中没有，则调用 imageClassificationUtil 获取 imgTags
                imgTags = imageClassificationUtil.ImagetoMsg(bytes);
                // 将结果存储到 Redis 中
                radisService.setValue(fileName, imgTags);
            }
            getWarningSearchimg.setSearchText(imgTags);
        }
        GetWarningSearch getWarningSearch=new GetWarningSearch();
        getWarningSearch.setSearchText(getWarningSearchimg.getSearchText());
        getWarningSearch.setAlertTypes(getWarningSearchimg.getAlertTypes());
        getWarningSearch.setCameraPosition(getWarningSearchimg.getCameraPosition());
        getWarningSearch.setStartTime(getWarningSearchimg.getStartTime());
        getWarningSearch.setEndTime(getWarningSearchimg.getEndTime());
        getWarningSearch.setPageNum(getWarningSearchimg.getPageNum());
        getWarningSearch.setPageSize(getWarningSearchimg.getPageSize());

        getWarningSearch.setPageNum(getWarningSearch.getPageNum()-1);

        Page<WarningTable> page = warningTableService.searchByAlertTypes(getWarningSearch);
        if (page.getContent().size() > 0) {
            return JSON.toJSONString(Result.success("获取成功", Math.toIntExact(page.getTotalElements()), page.getContent()));
        }
        return JSON.toJSONString(Result.success("获取失败", 0, page.getContent()));
    }


    @PostMapping("/selectwarning")
    public String searchWarnings(@RequestBody GetWarningSearch getWarningSearch) throws IOException {

        getWarningSearch.setPageNum(getWarningSearch.getPageNum()-1);

        Page<WarningTable> page = warningTableService.searchByAlertTypes(getWarningSearch);
        if (page.getContent().size() > 0) {
            return JSON.toJSONString(Result.success("获取成功", Math.toIntExact(page.getTotalElements()), page.getContent()));
        }
        return JSON.toJSONString(Result.success("获取失败", 0, page.getContent()));
    }

    @GetMapping("/getTodayTopAlertTypes")
    public String getTodayTopAlertTypes() {
        Map<String, Map<String, Long>> topAlertTypes = warningTableService.getTodayTopAlertTypes();
        return JSON.toJSONString(Result.success("获取成功", topAlertTypes.size(), topAlertTypes));
    }

    @GetMapping("/getSevenTopAlertTypes")
    public String getSevenTopAlertTypes() {
        Map<String, Map<String, Long>> topAlertTypes = warningTableService.getSevenTopAlertTypes();
        return JSON.toJSONString(Result.success("获取成功", topAlertTypes.size(), topAlertTypes));
    }

    @GetMapping("/getMonthTopAlertTypes")
    public String getTopAlertTypes() {
        Map<String, Map<String, Long>> topAlertTypes = warningTableService.getThreeDayTopAlertTypes();
        return JSON.toJSONString(Result.success("获取成功", topAlertTypes.size(), topAlertTypes));
    }




}

