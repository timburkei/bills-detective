import logging
import os

import cv2
import torch
from ultralytics import YOLO

'''
This class is responsible for managing the YOLO model, setting hyperparameters and defining functions, that can be executed on the model.
It is inistantiated as a Singleton class in the MainParser.py.
This ensures to not run into memory issues, because is model is loaded several times.
'''
class YoloModelManager:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not isinstance(cls._instance, cls):
            cls._instance = super(YoloModelManager, cls).__new__(cls)
        return cls._instance

    def __init__(self, model_path: str):
        self.model_path: str = model_path
        self.model = self.load()
        self.device = self._setDevice()

    def load(self):
        logging.info(f"Loading model from {self.model_path}")
        try:
            self.model = YOLO(self.model_path)
            # self.model.predict()
        except:
            raise Exception("Model not found.")

    def _setDevice(self):
        # Setup device-agnostic code
        if torch.cuda.is_available():
            device = "cuda"  # NVIDIA GPU
        elif torch.backends.mps.is_available():
            device = "mps"  # Apple GPU
        else:
            device = "cpu"  # Defaults to CPU if NVIDIA GPU/Apple GPU aren't available

        logging.info(f"Using device: {device} for YOLO inference.")
        return device

    def predict_image(self, image: bytes, image_name: str = None, image_size: int = 1280,
                      confidence_threshold: float = 0.4, agnostic_nms: bool = True, iou_threshold: float = 0.7):

        logging.info(f"Running YOLO prediction on image {image_name}")
        if self.model is None:
            self.load()
        try:
            return self.model.predict(source=image, imgsz=image_size, conf=confidence_threshold, device=self.device,
                                      agnostic_nms=agnostic_nms, iou=iou_threshold)
        except:
            print(f"Error in YOLO DETECTION on image: {image_name}")

    def visualize_prediction(self, prediction, text_size: int = 3, save_dir: str = None, save_name: str = None):
        logging.info(f"Visualizing YOLO prediction on image {save_name}")
        try:
            plot_img = prediction.plot(line_width=text_size)
            cv2.imwrite(os.path.join(save_dir, save_name), plot_img)
        except:
            raise Exception(f"Saving prediction {prediction} failed.")
