package com.yys.util;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MinioClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(MinioClientProvider.class);

    @Value("${stream.minio.endpoint}")
    private String endpoint;

    @Value("${stream.minio.access-key}")
    private String accessKey;

    @Value("${stream.minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient createMinioClient() {

        // 创建 MinioClient
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        // 测试连接是否成功
        try {
            minioClient.hashCode();
            logger.info("Minio客户端已成功连接.");
        } catch (Exception e) {
            logger.error("无法连接到Minio服务器: {}", e.getMessage(), e);
            throw e;
        }

        return minioClient;
    }

}

