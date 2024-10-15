package com.yys.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yys.service.StreamService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class StreamServiceimpl implements StreamService {

    @Value("${stream.python-url}")
    private String pythonUrl;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public String startStream(String[] rtspUrls, String[] modelPaths, String taskId) {
        String url = pythonUrl + "/start_stream";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder rtspUrlsBuilder = new StringBuilder("[");
        for (String urls : rtspUrls) {
            if ("0".equals(urls)) {
                rtspUrlsBuilder.append(0).append(",");
            } else {
                rtspUrlsBuilder.append("\"").append(urls).append("\",");
            }
        }
        if (rtspUrls.length > 0) {
            rtspUrlsBuilder.setLength(rtspUrlsBuilder.length() - 1);
        }
        rtspUrlsBuilder.append("]");

        String modelPathsJson = toJsonArray(modelPaths);

        // 拼接 JSON 字符串，包含 taskId
        String json = "{\"rtsp_urls\":" + rtspUrlsBuilder.toString() +
                ",\"model_paths\":" + modelPathsJson +
                ",\"task_id\":\"" + taskId + "\"}";

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        return restTemplate.postForObject(url, request, String.class);
    }


    @Override
    public String stopStream(String name) {
        String url = pythonUrl + "/stop_stream/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 正确构建JSON字符串
        String json = "{\"name\":\"" + name + "\"}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        return restTemplate.postForObject(url, request, String.class);
    }

    @SneakyThrows
    @Override
    public int processModelFile(File file) {
        String url = pythonUrl + "/up-model";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        // 检查上传结果
        if (response.getStatusCode().is2xxSuccessful()) {
            return 1; // 假设返回1表示上传成功
        } else {
            System.err.println("文件上传失败: " + response.getBody());
            return -1; // 失败返回-1
        }
    }

    @SneakyThrows
    @Override
    public List<String> getimgmsg(String modelpath,File file) {


        String url = pythonUrl + "/get-imgmsg";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(file));
        body.add("modeld_path", modelpath);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        List<String> list=new ArrayList<>();
        if (response.getStatusCode().is2xxSuccessful()){
            list = new ObjectMapper().readValue(response.getBody(), List.class);
        }
        return list;
    }

    @Override
    public boolean deleteModel(String modelPath) {

        String url = pythonUrl + "/delete-file";
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        // 正确构建JSON字符串
        String json = "{\"modelPath\":\"" + modelPath + "\"}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()){
           return true;
        }
        return false;
    }


    // 将数组转换为JSON数组字符串
    private String toJsonArray(String[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append("\"").append(array[i]).append("\"");
            if (i < array.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }


}
