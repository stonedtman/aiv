import cv2
import torch
import numpy as np
from utils.torch_utils import select_device
from models.experimental import attempt_load
from utils.general import check_img_size, non_max_suppression

def load_models(model_paths):
    models = []
    device = select_device('')  # 自动选择设备，如果有GPU则使用GPU
    for path in model_paths:
        model = attempt_load(path, device=device)
        models.append(model)
    imgsz = check_img_size((640, 480), s=models[0].stride.max())
    return models, device, imgsz

def prepare_image(frame, imgsz, device):
    img = cv2.resize(frame, imgsz)
    img = img[:, :, ::-1].transpose(2, 0, 1)
    img = np.ascontiguousarray(img)
    img = torch.from_numpy(img).to(device)
    img = img.float() / 255.0
    if img.ndimension() == 3:
        img = img.unsqueeze(0)
    return img

def detect_objects(model, img):
    pred = non_max_suppression(model(img)[0], conf_thres=0.5, iou_thres=0.45, agnostic=False)
    return pred
