import os
import cv2
import platform
import subprocess
import time
from utils.myutils import create_output_dirs, setup_rtsp_streams, send_json_record
from utils.model_loader import load_models, prepare_image, detect_objects
from utils.createbox import scale_coords, plot_one_box
from utils.plots import colors
from utils.uploader import initialize_minio_client, setup_minio_bucket, upload_to_minio

# 初始化MinIO客户端
endpoint = "192.168.71.25:9000"
access_key = "admin"
secret_key = "admin123"
minio_client = initialize_minio_client(endpoint, access_key, secret_key, secure=False)
setup_minio_bucket(minio_client)
create_output_dirs()

# 处理平台兼容性
plt = platform.system()
if plt == 'Windows':
    import pathlib
    pathlib.PosixPath = pathlib.WindowsPath

def save_frames_to_disk(frames, output_dir):
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    for idx, frame in enumerate(frames):
        cv2.imwrite(os.path.join(output_dir, f"frame_{idx:04d}.png"), frame)


def encode_video_with_ffmpeg(output_dir, output_file):
    ffmpeg_command = [
        'ffmpeg', '-y', '-framerate', '30', '-i', os.path.join(output_dir, 'frame_%04d.png'),
        '-c:v', 'libx264', '-pix_fmt', 'yuv420p', output_file
    ]
    subprocess.run(ffmpeg_command)


def process_video_stream(name, rtsp_urls, model_paths, stop_event, taskId):
    # 加载模型
    models, device, imgsz = load_models(model_paths)

    # 使用OpenCV打开RTSP视频流
    cap_objects, cap_properties = setup_rtsp_streams(rtsp_urls)

    # 初始化参数
    buffer_frames = [[] for _ in range(len(rtsp_urls))]
    detection_active = [False] * len(rtsp_urls)
    inactive_counters = [0] * len(rtsp_urls)
    max_inactive_frames = 30  # 定义多少帧没有检测到目标则认为行为结束
    first_detected_frames = [None] * len(rtsp_urls)  # 用于保存检测到目标的第一帧

    while not stop_event.is_set():
        frames = []
        for cap in cap_objects:
            ret, frame = cap.read()
            if not ret:
                break
            frames.append(frame)

        if len(frames) == 0:
            break

        for i, frame in enumerate(frames):
            detected_any = False
            for model in models:
                img = prepare_image(frame, imgsz, device)
                pred = detect_objects(model, img)
                detected = False
                for det in pred:
                    if len(det):
                        det[:, :4] = scale_coords(img.shape[2:], det[:, :4], frame.shape).round()
                        for *xyxy, conf, cls in det:
                            if conf > 0.7:  # 只在置信度大于0.7时进行处理
                                detected = True
                                label = f'{model.names[int(cls)]} {conf:.2f}'
                                plot_one_box(xyxy, frame, label=label, color=colors(int(cls), True))

                if detected:
                    detected_any = True
                    if not detection_active[i]:
                        detection_active[i] = True
                        buffer_frames[i] = []
                        first_detected_frames[i] = frame.copy()  # 保存第一帧用于上传图片
                    buffer_frames[i].append(frame)
                    inactive_counters[i] = 0
                else:
                    if detection_active[i]:
                        inactive_counters[i] += 1
                        if inactive_counters[i] >= max_inactive_frames:
                            detection_active[i] = False
                            # 保存帧到磁盘
                            output_dir = f"temp_frames_{i}"
                            save_frames_to_disk(buffer_frames[i], output_dir)

                            # 使用ffmpeg编码视频
                            video_filename = f"output_{time.time()}_{i}.mp4"
                            output_file = os.path.join("output_videos", video_filename)
                            encode_video_with_ffmpeg(output_dir, output_file)

                            # 保存并上传第一帧图片
                            image_filename = f"frame_{time.time()}_{i}.jpg"
                            image_path = os.path.join("output_img", image_filename)
                            cv2.imwrite(image_path, first_detected_frames[i])
                            i_path = upload_to_minio(image_path, "img", image_filename, minio_client)

                            # 上传视频到 MinIO
                            v_path = upload_to_minio(output_file, "video", video_filename, minio_client)
                            video_path = f"http://{endpoint}/{v_path}"
                            img_path = f"http://{endpoint}/{i_path}"
                            send_json_record(video_path, img_path, rtsp_urls[i], time.time(), model_paths, taskId, i)

                            # 清理临时帧
                            buffer_frames[i] = []
                            first_detected_frames[i] = None
                            inactive_counters[i] = 0

    # 释放资源
    for cap in cap_objects:
        cap.release()
    cv2.destroyAllWindows()
