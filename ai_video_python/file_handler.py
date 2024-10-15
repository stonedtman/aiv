import os
import zipfile
from shutil import rmtree
from werkzeug.utils import secure_filename

from flask import jsonify, send_file


# 定义基础目录
BASE_DIR = os.getcwd()  # 获取当前工作目录

# 使用os.path.join构造路径
UPLOAD_FOLDER = os.path.join(BASE_DIR, 'uploads')
DATA_FOLDER = os.path.join(BASE_DIR, 'data')
MODEL_FOLDER = os.path.join(BASE_DIR, 'runs', 'train')
UPLOAD_MODEL = os.path.join(BASE_DIR, 'upweights')
UPLOAD_IMG = os.path.join(BASE_DIR, 'text', 'textimg')

def upload_file(request):
    try:
        # 检查请求中是否有文件部分
        if 'file' not in request.files:
            return '请求中没有文件部分', 400

        file = request.files['file']
        if file.filename == '':
            return '没有文件', 400

        if file:
            # 检查并创建上传目录
            if not os.path.exists(UPLOAD_FOLDER):
                os.makedirs(UPLOAD_FOLDER)

            # 保存文件到上传目录
            file_path = os.path.join(UPLOAD_FOLDER, file.filename)
            file.save(file_path)

            # 检查并创建数据目录
            if not os.path.exists(DATA_FOLDER):
                os.makedirs(DATA_FOLDER)

            # 获取压缩包名
            zip_folder_name = os.path.splitext(file.filename)[0]
            zip_extract_folder = os.path.join(DATA_FOLDER, zip_folder_name)

            # 如果压缩包名的文件夹已经存在，则删除它
            if os.path.exists(zip_extract_folder):
                rmtree(zip_extract_folder)

            # 创建压缩包名的文件夹
            os.makedirs(zip_extract_folder)

            # 解压文件到 data 目录下的压缩包名的文件夹下
            with zipfile.ZipFile(file_path, 'r') as zip_ref:
                zip_ref.extractall(zip_extract_folder)

            # 清理上传的 ZIP 文件
            os.remove(file_path)

            return jsonify({'message': '上传文件成功，并已解压'}), 200

    except Exception as e:
        return f'发生错误: {str(e)}', 500


def upload_image(request):
    """
    上传图片并返回相对路径。

    :param request: Flask 请求对象
    :return: 保存图片的相对路径或 None
    """
    # 检查请求中是否有文件部分
    if 'image' not in request.files:
        return None

    file = request.files['image']
    if file.filename == '':
        return None

    if file:
        # 检查并创建上传目录
        if not os.path.exists(UPLOAD_IMG):
            os.makedirs(UPLOAD_IMG)

        # 构建问文件存储路径
        file_path = os.path.join(UPLOAD_IMG, file.filename)

        # 保存文件到上传目录
        file.save(file_path)

        return file_path

    return None

def delete_image(file_path):
    """
    根据文件路径删除图片。

    :param image_path: 文件的相对路径
    :return: 删除成功返回 True，否则返回 False
    """
    # 构建绝对路径
    absolute_path = os.path.join(os.getcwd(), file_path)

    # 检查文件是否存在
    if os.path.exists(absolute_path):
        os.remove(absolute_path)
        return True
    else:
        return False


def upload_models(request):
    # 检查请求中是否有文件部分
    if 'file' not in request.files:
        return '请求中没有文件部分', 400


    file = request.files['file']
    if file.filename == '':
        return '没有文件', 400


    if file:
        # 使用secure_filename确保文件名安全
        safe_filename = secure_filename(file.filename)
        if safe_filename == '':
            return '无效的文件名', 400

        # 检查并创建上传目录
        try:
            if not os.path.exists(UPLOAD_MODEL):
                os.makedirs(UPLOAD_MODEL)
        except Exception as e:
            return f'创建上传目录失败: {str(e)}', 500

        # 保存文件到上传目录
        file_path = os.path.join(UPLOAD_MODEL, safe_filename)
        try:
            file.save(file_path)
        except Exception as e:
            return f'保存文件失败: {str(e)}', 500

        return '文件上传成功', 200


def tosend_file(request):
    data = request.json
    path = data.get('path')

    model_path = os.path.join(MODEL_FOLDER, path, 'weights', 'best.pt')

    # 假设文件存在并可以访问
    if model_path and os.path.exists(model_path):
        return send_file(model_path, as_attachment=True)
    else:
        return {"message": "File not found"}, 404
