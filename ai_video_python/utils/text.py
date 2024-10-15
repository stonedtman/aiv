from minio import Minio
from minio.error import S3Error
import time

def get_bucket_name():
    # 根据当前年份生成桶名称
    return f"warningdata{time.strftime('%Y')}"

def upload_to_minio(file_path, file_type, object_name, minio_client, content_type):
    try:
        today = time.strftime('%Y-%m-%d')
        current_month = time.strftime('%m')
        bucket_name = get_bucket_name()
        object_name = f"{file_type}/{current_month}/{today}/{object_name}"
        minio_client.fput_object(bucket_name, object_name, file_path, content_type=content_type)
        print(f"Successfully uploaded {object_name} to {bucket_name}")
        return f"{bucket_name}/{object_name}"
    except S3Error as e:
        print(f"Failed to upload {object_name} to {bucket_name}: {e}")
        return None

# 初始化 MinIO 客户端
minio_client = Minio(
    "your-minio-server:9000",
    access_key="your-access-key",
    secret_key="your-secret-key",
    secure=False  # 如果使用 HTTP 而不是 HTTPS，则设置为 False
)

# 上传视频文件
video_file_path = "path/to/your/video.mp4"
video_filename = "your-video-file.mp4"
video_path = upload_to_minio(video_file_path, "video", video_filename, minio_client, "video/mp4")

# 上传图片文件
image_file_path = "path/to/your/image.jpg"
image_filename = "your-image-file.jpg"
img_path = upload_to_minio(image_file_path, "img", image_filename, minio_client, "image/jpeg")

print(f"视频文件路径: {video_path}")
print(f"图片文件路径: {img_path}")
