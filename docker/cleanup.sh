#!/bin/bash

# 停止并删除 docker-compose 中定义的所有服务及容器
echo "正在停止并删除 docker-compose 服务..."
docker-compose down --volumes

# 删除相关镜像
echo "正在删除 docker-compose 中定义的镜像..."
docker rmi -f mysql:8.0.38 redis:7.2 elasticsearch:8.14.3 minio/minio:latest rabbitmq:4.0 ai_video:v1.3

# 清理未使用的镜像、网络和卷
echo "清理未使用的镜像、网络和卷..."
docker system prune -f --volumes

echo "清理完成！"
