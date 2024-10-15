import os
import time
import io
import json
from minio import Minio
from minio.error import S3Error

from utils.yamlConfig import read_config

config=read_config()
def initialize_minio_client():
    minio_config = config['minio']
    endpoint = minio_config['endpoint']
    access_key = minio_config['access_key']
    secret_key = minio_config['secret_key']
    secure = minio_config['secure']
    return Minio(endpoint, access_key=access_key, secret_key=secret_key, secure=secure)


def get_bucket_name():
    # 使用当前年份生成桶名称
    current_year = time.strftime('%Y')
    bucket_name = f'warningdata{current_year}'
    return bucket_name

def setup_minio_bucket(minio_client):
    bucket_name = get_bucket_name()
    if not minio_client.bucket_exists(bucket_name):
        minio_client.make_bucket(bucket_name)
    print(f"桶 {bucket_name} 已经创建.")

    # 创建以月份为单位的文件夹
    today = time.strftime('%Y-%m-%d')
    current_month = time.strftime('%m')

    # 使用 io.BytesIO 代替空字节
    empty_file = io.BytesIO()
    minio_client.put_object(bucket_name, f"video/{current_month}/{today}/", empty_file, length=0)
    minio_client.put_object(bucket_name, f"img/{current_month}/{today}/", empty_file, length=0)
    print(f"视频文件夹/{current_month}/{today}/ 和 图片文件夹/{current_month}/{today}/ 创建在 {bucket_name}.")


def upload_to_minio(file_path, file_type, object_name, minio_client):
    try:
        today = time.strftime('%Y-%m-%d')
        current_month = time.strftime('%m')
        bucket_name = get_bucket_name()
        object_name = f"{file_type}/{current_month}/{today}/{object_name}"
        minio_client.fput_object(bucket_name, object_name, file_path )
        print(f"已成功上传 {object_name} to {bucket_name}")

        # 删除本地文件
        if file_type in ['video', 'img'] and os.path.exists(file_path):
            os.remove(file_path)
            print(f"本地文件 {file_path} 已经删除.")

        return f"{bucket_name}/{object_name}"
    except S3Error as e:
        print(f"上传失败 {object_name} to {bucket_name}: {e}")
        return None


def generate_json(video_path, img_path, model_name):
    data = {
        "video_path": video_path,
        "img_path": img_path,
        "created_time": time.strftime('%Y-%m-%d %H:%M:%S'),
        "model_name": model_name
    }
    json_data = json.dumps(data, ensure_ascii=False, indent=4)
    json_filename = f"output_json/{time.strftime('%Y-%m-%d_%H-%M-%S')}.json"
    with open(json_filename, 'w', encoding='utf-8') as f:
        f.write(json_data)
    print(f"json文件已经创建: {json_filename}")

