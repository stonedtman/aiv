import os
import cv2
import json
import time
from rabbitmq.rabbitmq_utils import send_to_rabbitmq

def split_string(model_paths):
    if not isinstance(model_paths, (list, tuple)):
        model_paths = [model_paths]
    return [os.path.basename(path) for path in model_paths]

def create_output_dirs():
    output_dirs = ["output_videos", "output_json", "output_img"]
    for output_dir in output_dirs:
        os.makedirs(output_dir, exist_ok=True)

def create_video_writer(frame_width, frame_height, index, fourcc, fps, first_frame):
    video_filename = f'output_{index}_{int(time.time())}.mp4'
    video_file = os.path.join("output_videos", video_filename)
    video_writer = cv2.VideoWriter(video_file, fourcc, fps, (frame_width, frame_height))

    image_filename = f'first_frame_{index}_{int(time.time())}.jpg'
    image_file = os.path.join("output_img", image_filename)
    cv2.imwrite(image_file, first_frame)

    return video_writer, video_filename, image_filename

def setup_rtsp_streams(rtsp_urls):
    cap_objects = [cv2.VideoCapture(rtsp_url) for rtsp_url in rtsp_urls]
    cap_properties = []
    for cap in cap_objects:
        if not cap.isOpened():
            raise IOError("无法打开RTSP流")
        frame_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
        frame_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
        cap_properties.append((frame_width, frame_height))
    return cap_objects, cap_properties

def show_frame(frame, display_width, display_height, i):
    resized_frame = cv2.resize(frame, (display_width, display_height))
    cv2.imshow(f"RTSP Stream {i}", resized_frame)
    cv2.resizeWindow(f"RTSP Stream {i}", display_width, display_height)

def send_json_record(video_filename, image_filename, video_stream, timestamp, model_paths,taskId, index):
    record = {
        "videoPath": video_filename,
        "imgPath": image_filename,
        "rtspUrl": video_stream,
        "time": time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(timestamp)),
        "model": split_string(model_paths),
        "taskId": taskId,
        "index": index
    }
    send_to_rabbitmq('json_queue', record)








