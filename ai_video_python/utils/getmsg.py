import cv2
import torch
from pathlib import Path

def get_img_msg(imgpath, modelpath):
    # 加载模型
    model = torch.hub.load('ultralytics/yolov5', 'custom', path=modelpath)

    # 获取模型的类别名称
    names = model.names

    # 进行预测
    results = model(imgpath)

    # 创建一个空列表来存储检测结果
    detections = []

    # 遍历检测结果
    for *xyxy, conf, cls in results.xyxy:
        class_id = int(cls.item())  # 获取类别 ID
        confidence = float(conf.item())  # 获取置信度
        xyxy = [float(x) for x in xyxy]  # 获取边界框坐标

        # 获取类别名称
        class_name = names[class_id]

        # 构建检测框的信息字典
        detection_info = {
            "class_name": class_name,
            "confidence": confidence,
            "coordinates": xyxy
        }

        # 将检测框信息添加到列表中
        detections.append(detection_info)

    # 读取图片并获取其尺寸
    image = cv2.imread(imgpath)
    height, width, _ = image.shape

    # 创建一个空列表来存储转换后的检测框信息
    yolo_detections = []

    # 遍历每个检测框
    for detection in detections:
        class_name = detection["class_name"]
        confidence = detection["confidence"]
        xyxy = detection["coordinates"]

        # 计算中心点坐标和宽高
        x_center = (xyxy + xyxy[2]) / 2.0
        y_center = (xyxy[1] + xyxy[3]) / 2.0
        plot_width = xyxy[2] - xyxy
        plot_height = xyxy[3] - xyxy[1]

        # 归一化坐标
        x_center /= width
        y_center /= height
        plot_width /= width
        plot_height /= height

        # 构建 YOLOv 格式的字符串
        yolo_line = f"{class_name},{x_center:.6f},{y_center:.6f},{plot_width:.6f},{plot_height:.6f},{confidence:.2f}"

        # 将转换后的检测框信息添加到列表中
        yolo_detections.append(yolo_line)

    return yolo_detections

