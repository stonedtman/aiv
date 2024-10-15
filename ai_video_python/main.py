import os
import time
import cv2
import platform
from utils.myutils import create_output_dirs, create_video_writer, setup_rtsp_streams, show_frame, send_json_record
from utils.model_loader import load_models, prepare_image, detect_objects
from utils.createbox import scale_coords, plot_one_box
from utils.plots import colors
from utils.uploader import initialize_minio_client, setup_minio_bucket, upload_to_minio

# 处理路径兼容性
plt = platform.system()
if plt == 'Windows':
    import pathlib
    pathlib.PosixPath = pathlib.WindowsPath

endpoint = "127.0.0.1:9005"
access_key = "admin"
secret_key = "admin123456"
# MinIO 客户端配置
minio_client = initialize_minio_client(endpoint,access_key,secret_key,secure=False)

# 设置桶和文件夹
setup_minio_bucket(minio_client)

# 创建输出目录
create_output_dirs()

# 设置编解码器和视频参数
fourcc = cv2.VideoWriter_fourcc(*'avc1')
fps = 30.0

# 设定RTSP视频流的URL
rtsp_urls = [0]  # 假设有一个视频流

# 加载模型
model_paths = ['weights/Smartphone.pt']
models, device, imgsz = load_models(model_paths)

# 使用OpenCV打开RTSP视频流
cap_objects, cap_properties = setup_rtsp_streams(rtsp_urls)

# 初始化参数
out_writers = [None] * len(rtsp_urls)
buffer_frames = [[] for _ in range(len(rtsp_urls))]
detection_active = [False] * len(rtsp_urls)
inactive_counters = [0] * len(rtsp_urls)
max_inactive_frames = 30  # 定义多少帧没有检测到目标则认为行为结束

# 标志位，用于检测流是否仍然活跃
stream_active = True

# 固定显示窗口大小
display_width = 640
display_height = 480

while stream_active:
    frames = []
    for cap in cap_objects:
        ret, frame = cap.read()
        if not ret:
            stream_active = False
            break
        frames.append(frame)

    if not stream_active or len(frames) == 0:
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
                    video_writer, video_filename, image_filename = create_video_writer(cap_properties[i][0], cap_properties[i][1], i, fourcc, fps, frame)
                    out_writers[i] = video_writer
                buffer_frames[i].append(frame)
                inactive_counters[i] = 0
            else:
                if detection_active[i]:
                    inactive_counters[i] += 1
                    if inactive_counters[i] >= max_inactive_frames:
                        detection_active[i] = False
                        for buffer_frame in buffer_frames[i]:
                            out_writers[i].write(buffer_frame)
                        out_writers[i].release()
                        out_writers[i] = None
                        buffer_frames[i] = []
                        inactive_counters[i] = 0
                        # 上传视频和图片到 MinIO
                        video_file_path = os.path.join("output_videos", video_filename)
                        image_file_path = os.path.join("output_img", image_filename)
                        v_path=upload_to_minio(video_file_path,"video", video_filename, minio_client)
                        i_path=upload_to_minio(image_file_path,"img", image_filename, minio_client)
                        video_path = f"http://{endpoint}/{v_path}"
                        img_path = f"http://{endpoint}/{i_path}"
                        send_json_record(video_path, img_path,rtsp_urls[i], time.time(), model_paths, i)
                    else:
                        buffer_frames[i].append(frame)

        # 调整显示的帧大小
        show_frame(frame, display_width, display_height, i)

    if cv2.waitKey(1) == ord('q'):
        stream_active = False
        break

# 释放资源
for cap in cap_objects:
    cap.release()
for out in out_writers:
    if out:
        out.release()
cv2.destroyAllWindows()




