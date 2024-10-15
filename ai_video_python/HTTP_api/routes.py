from flask import request, jsonify
from HTTP_api.thread_manager import start_thread, stop_thread
from file_handler import upload_file, tosend_file, upload_models, upload_image, delete_image
from utils.getmsg import get_img_msg

def setup_routes(app):
    @app.route('/start_stream', methods=['POST'])
    def start_stream():
        data = request.get_json()
        rtsp_urls = data.get('rtsp_urls')
        model_paths = data.get('model_paths')
        taskId = data.get('task_id')

        if not rtsp_urls or not model_paths:
            return jsonify({"error": "rtsp_urls和model_paths是必需的"}), 400

        name = start_thread(rtsp_urls, model_paths,taskId)
        return jsonify({"thread_name": name})

    @app.route('/stop_stream/', methods=['POST'])
    def stop_stream():
        data = request.get_json()
        name = data.get('name')
        result = stop_thread(name)
        if result:
            return jsonify({"status": "已停止"}), 200
        else:
            return jsonify({"error": "线程未找到或未运行"}), 404

    @app.route('/upload', methods=['POST'])
    def upload_file_endpoint():
        return upload_file(request)

    @app.route('/get-file', methods=['POST'])
    def get_file():
        return tosend_file(request)

    @app.route('/up-model', methods=['POST'])
    def up_model():
        return upload_models(request)

    @app.route('/get-imgmsg', methods=['POST'])
    def get_imgmsg():
        imgpath=upload_image(request)
        if not imgpath:
            return jsonify({"error": "未找到图片"}), 404
        modelpath = request.form.get('modeld_path')
        result = get_img_msg(imgpath,modelpath)
        delete_image(imgpath)
        return jsonify(result),200

    @app.route('/delete-file', methods=['POST'])
    def delete_file():

        file_path = request.json.get('modelPath')
        result=delete_image(file_path)
        if result:
            return jsonify({"message": "文件已删除"}), 200
        return jsonify({"error": "文件未找到"}), 404