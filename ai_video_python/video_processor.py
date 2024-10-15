import os
import shutil
import cv2
import platform
import subprocess
import time
from utils.myutils import create_output_dirs, setup_rtsp_streams, send_json_record
from utils.model_loader import load_models, prepare_image, detect_objects
from utils.createbox import scale_coords, plot_one_box
from utils.plots import colors
from utils.uploader import initialize_minio_client, setup_minio_bucket, upload_to_minio
import yaml

# 初始化 MinIO 客户端
minio_client = initialize_minio_client()
setup_minio_bucket(minio_client)
create_output_dirs()

# 处理平台兼容性
plt = platform.system()
if plt == 'Windows':
    import pathlib
    pathlib.PosixPath = pathlib.WindowsPath

def save_frames_to_disk(frames, temp_dir):
    if not os.path.exists(temp_dir):
        os.makedirs(temp_dir)
    for idx, frame in enumerate(frames):
        cv2.imwrite(os.path.join(temp_dir, f"frame_{idx:04d}.png"), frame)

def encode_video_with_ffmpeg(temp_dir, output_file):
    ffmpeg_command = [
        'ffmpeg', '-y', '-i', os.path.join(temp_dir, 'frame_%04d.png'),
        '-c:v', 'libx264', '-pix_fmt', 'yuv420p', output_file
    ]
    subprocess.run(ffmpeg_command)

def cleanup_temp_files(temp_dir):
    if os.path.exists(temp_dir):
        shutil.rmtree(temp_dir)

def process_video_stream(name, rtsp_urls, model_paths, stop_event, taskId):
    # 加载模型
    models, device, imgsz = load_models(model_paths)

    # 使用 OpenCV 打开 RTSP 视频流
    cap_objects, cap_properties = setup_rtsp_streams(rtsp_urls)

    # 初始化参数
    buffer_frames = [[] for _ in range(len(rtsp_urls))]
    recording = [False] * len(rtsp_urls)
    frame_count = [0] * len(rtsp_urls)
    first_detected_frames = [None] * len(rtsp_urls)
    print("开始处理视频流...")# 保存检测到目标的第一帧

    temp_dir = "temp_frames"
    if not os.path.exists(temp_dir):
        os.makedirs(temp_dir)

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
                            if conf > 0.7:  # 置信度大于0.7时处理
                                detected = True
                                label = f'{model.names[int(cls)]} {conf:.2f}'
                                plot_one_box(xyxy, frame, label=label, color=colors(int(cls), True))

                if detected:
                    detected_any = True
                    if not recording[i]:
                        recording[i] = True
                        frame_count[i] = 0  # 重置帧计数
                        buffer_frames[i] = []  # 清空缓冲区
                        first_detected_frames[i] = frame.copy()
                        print(f"开始录制视频流 {i}...")# 保存第一帧
                    buffer_frames[i].append(frame)
                else:
                    if recording[i]:
                        buffer_frames[i].append(frame)
                        frame_count[i] += 1
                        if frame_count[i] >= 30:  # 检查是否已处理30帧
                            recording[i] = False

                            # 保存帧到磁盘
                            save_frames_to_disk(buffer_frames[i], temp_dir)
                            print(f"录制视频流 {i} 已完成...")

                            # 使用 ffmpeg 编码视频
                            timestamp = int(time.time())
                            video_filename = f"output_{timestamp}_{taskId}_{i}.mp4"
                            output_file = os.path.join("output_videos", video_filename)
                            encode_video_with_ffmpeg(temp_dir, output_file)
                            print(f"已保存视频 {output_file}")

                            # 保存并上传第一帧图片
                            image_filename = f"frame_{timestamp}_{taskId}_{i}.jpg"
                            image_path = os.path.join("output_img", image_filename)
                            cv2.imwrite(image_path, first_detected_frames[i])
                            i_path = upload_to_minio(image_path, "img", image_filename, minio_client)
                            print(f"已保存并上传第一帧图片 {image_path} 到 MinIO")

                            # 上传视频到 MinIO
                            v_path = upload_to_minio(output_file, "video", video_filename, minio_client)
                            video_path = f"/{v_path}"
                            img_path = f"/{i_path}"
                            send_json_record(video_path, img_path, rtsp_urls[i], timestamp, model_paths, taskId, i)



                            # 清空缓冲区，防止旧帧残留
                            buffer_frames[i] = []
                            first_detected_frames[i] = None
                            frame_count[i] = 0  # 重置不活动计数器

                            # 删除临时文件
                            cleanup_temp_files(temp_dir)

    # 释放资源
    for cap in cap_objects:
        cap.release()
    cv2.destroyAllWindows()



