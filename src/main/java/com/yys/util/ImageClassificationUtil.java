package com.yys.util;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class ImageClassificationUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageClassificationUtil.class);

    private static final String URL = "https://nlp.stonedt.com/api/classpic";
    private static final String SECRET_ID = "a0d96003-3a11-4e78-b932-8bfc999b6a6c";
    private static final String SECRET_KEY = "60a4f6e68536c6381d64ad86ee7b4ac9";

    private final RestTemplate restTemplate;
    private final MinioClient minioClient;

    public ImageClassificationUtil(RestTemplate restTemplate, MinioClient minioClient) {
        this.restTemplate = restTemplate;
        this.minioClient = minioClient;
    }

    public String classifyImage(String minioFilePath) {
        try {
            String bucketName = extractBucketName(minioFilePath);
            String objectName = extractObjectName(minioFilePath);
            byte[] imageBytes = downloadImageFromMinio(bucketName, objectName);

            // 设置HTTP请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("secret-id", SECRET_ID);
            headers.set("secret-key", SECRET_KEY);

            // 创建请求体，包含要分类的图片
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("images", new ByteArrayResourceWithFileName(imageBytes, "img.jpg"));

            // 创建带有请求头和请求体的HTTP实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 发送HTTP请求并获取响应
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, requestEntity, Map.class);

            // 提取并返回分类结果关键词
            return extractKeywords(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("图片处理或请求失败: " + e.getMessage(), e);
        }
    }

    public String ImagetoMsg(byte[] bytes) {
        try {
            byte[] imageBytes = bytes;

            // 设置HTTP请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("secret-id", SECRET_ID);
            headers.set("secret-key", SECRET_KEY);

            // 创建请求体，包含要分类的图片
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("images", new ByteArrayResourceWithFileName(imageBytes, "img.jpg"));

            // 创建带有请求头和请求体的HTTP实体
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 发送HTTP请求并获取响应
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, requestEntity, Map.class);

            // 提取并返回分类结果关键词
            return extractKeywords(response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("图片处理或请求失败: " + e.getMessage(), e);
        }
    }

    private String extractBucketName(String minioFilePath) {
        return minioFilePath.split("/")[1];
    }

    private String extractObjectName(String minioFilePath) {
        int firstSlashIndex = minioFilePath.indexOf("/");
        int secondSlashIndex = minioFilePath.indexOf("/", firstSlashIndex + 1);
        return minioFilePath.substring(secondSlashIndex + 1);
    }

    private byte[] downloadImageFromMinio(String bucketName, String objectName) throws Exception {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        } catch (MinioException e) {
            throw new RuntimeException("无法从MinIO下载图片: " + e.getMessage(), e);
        }
    }

    private String extractKeywords(Map<String, Object> responseBody) {
        if (responseBody == null || !(responseBody instanceof Map)) {
            throw new IllegalArgumentException("Invalid response body format.");
        }

        Map<String, Object> results = (Map<String, Object>) responseBody.get("results");

        if (results == null || !results.containsKey("result")) {
            return "";
        }

        Object resultObj = results.get("result");
        if (!(resultObj instanceof List)) {
            throw new IllegalArgumentException("Invalid 'result' format in the response.");
        }

        List<Map<String, Object>> result = (List<Map<String, Object>>) resultObj;

        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : result) {
            if (item != null && item.containsKey("keyword") && item.get("keyword") != null) {
                sb.append(item.get("keyword")).append(",");
            }
        }
        // 移除最后一个逗号
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();

    }

    static class ByteArrayResourceWithFileName extends ByteArrayResource {

        private final String fileName;

        public ByteArrayResourceWithFileName(byte[] byteArray, String fileName) {
            super(byteArray);
            this.fileName = fileName;
        }

        @Override
        public String getFilename() {
            return this.fileName;
        }
    }
}

