import os
from dataclasses import asdict

import imutils
import numpy as np
import cv2
from DataModels.ModelReceipt import StoreInfo, Item
from DataModels.ModelYOLO import YoloModelManager
from PostProcessor.CorrectorOCR import optimize_image_for_ocr
from PostProcessor.LineMatcher import extract_yolo_lines_2, draw_yolo_line_boxes
from PostProcessor.PytesseractOCR import pytesseract_read_text

import configparser

from Preprocessor.ImageStore import ImageStore

# Create a ConfigParser object
config = configparser.ConfigParser()
# Read the .config file
config.read('.config')
# Access the setting
img_height = int(config.get('DEFAULT', 'IMG_HEIGHT'))


model_path = "Model/yoloV8_M_detection.pt"
model_finetuned_path = "Model/yoloV8_M_detection_finetuned.pt"
yolo_detector = YoloModelManager(model_finetuned_path)
yolo_detector.load()


def extract_text_from_image(ocr_img, box, label):
    '''
    This function extracts the text from a single detected box from the image using pytesseract.
    :param ocr_img: Image that is optimized for OCR
    :param box: the detected box
    :param label: the predicted label of the box
    :return: The text that was detected in the box
    '''
    x1, y1, x2, y2 = [int(coord) for coord in box['box']]
    roi = ocr_img[y1:y2, x1:x2]
    upscaled_roi = cv2.resize(roi, None, fx=4, fy=4, interpolation=cv2.INTER_CUBIC)
    text = pytesseract_read_text(image=upscaled_roi, label=label)
    if not isinstance(text, float):
        text = text.replace('\n', ' ')

    return text


def doc_parser( img_name: str, image_store: ImageStore):
    '''
    This function is the main function of the Document Parser.
    It takes an image name and an ImageStore object as input and returns the parsed information and the ImageStore object.
    :param img_name:
    :param image_store:
    :return:
    '''
    # Get the images for the yolo prediction and the ocr from the ImageStore, that is provided as input
    preprocessed_image = image_store.get_image("optimized")
    transformed_image = image_store.get_image("transformed")

    # Convert the images from RGB to BGR, because Yolo and OpenCV expect the format to be BGR
    preprocessed_image = cv2.cvtColor(preprocessed_image, cv2.COLOR_RGB2BGR)
    transformed_image = cv2.cvtColor(transformed_image, cv2.COLOR_RGB2BGR)

    # Run the Yolo prediction on the preprocessed image
    # Used a large input size to get a better prediction for smaller objects on the image
    # Set the threshold to 0.5 to get a good balance between getting the right number of boxes and not getting too many false positives
        # This was set after testing different thresholds manually
    # Set the iou threshold to 0.6. So boxes that overlap more than 60% are merged into one box. This is used to avoid duplicate boxes.
    results = yolo_detector.predict_image(image=preprocessed_image,
                                          image_name=img_name,
                                          image_size=1280,
                                          confidence_threshold=0.5,
                                          agnostic_nms=True,
                                          iou_threshold=0.6)

    if results is None:
        return None

    # Move the results from the GPU to the CPU
    results = results[0].to('cpu')

    # Visualize the prediction for analysis
    plot_img = results.plot(line_width=2)
    plot_img = imutils.resize(plot_img, height=img_height)
    image_store.add_image("plot", plot_img)

    # Upscale the images and the bounding boxes. This is used to help ocr better detecting the text within the boxes.
    scaled_img, ocr_img, product_boxes, info_boxes = scale_image_and_boxes(yolo_results=results,
                                                                           img_name=img_name,
                                                                           transformed_image=transformed_image,
                                                                           desired_height=img_height)


    # Find corrresponding boxes that include the name and the price of a product.
    # This creates a line for each product.
    if len(product_boxes) > 0:
        lines = extract_yolo_lines_2([product_boxes])
    else:
        lines = []

    store_info = StoreInfo()

    # Further Optimize the image for OCR
    ocr_img = optimize_image_for_ocr(ocr_img, img_name)

    # Draw the lines on the image for visualization and analysis
    ocr_with_lines = draw_yolo_line_boxes(lines, ocr_img, img_name)
    image_store.add_image("ocr_with_lines", ocr_with_lines)

    # Extract the text from the boxes and add it to the StoreInfo object
    for box in [box for box in info_boxes if box['class'] != 'nameItem' and box['class'] != 'priceItem']:
        label = box['class']
        detected_text = extract_text_from_image(ocr_img, box, label)
        if hasattr(store_info, label):
            setattr(store_info, label, detected_text)

    # Add the product information for each product / line to the StoreInfo object
    for line in lines:
        item_info = {"nameItem": "DER GERÄT", "priceItem": 1.00}
        for box in line:
            label = box['class']
            detected_text = extract_text_from_image(ocr_img, box, label)
            item_info[label] = detected_text
        if item_info["nameItem"] == "" or item_info["nameItem"] == " ": item_info["nameItem"] = "DER GERÄT"
        item = Item(name=item_info["nameItem"], price=item_info["priceItem"])
        store_info.invoiceItems.append(item)

    if store_info.totalAmount == 0.0:
        store_info.totalAmount = store_info.calculate_total_amount()

    return asdict(store_info), image_store


def scale_image_and_boxes(yolo_results, img_name, transformed_image, desired_height=img_height):
    """
    This function scales the images from the yolo_prediction and for the ocr to the same height.
    Secoundly the bounding boxes from the prediction are scaled to the same height.
    Also, it splits the bounding boxes into two lists. One for the boxes that contain the name and the price of a product.
    Another one for all other boxes.
    :param yolo_results:
    :param img_name:
    :param desired_height:
    :return: Image from Yolo prediction, Image for OCR, List of boxes for the product information, List of boxes for the other information
    """
    # Get the original image from the results
    img = yolo_results.orig_img

    # Get the original image's height and width
    original_height, original_width = img.shape[:2]

    # Calculate the scaling factor
    scaling_factor = desired_height / original_height

    # Resize the image
    yolo_img_resized = cv2.resize(img, None, fx=scaling_factor, fy=scaling_factor, interpolation=cv2.INTER_AREA)

    # Scale the bounding boxes
    info_boxes = []
    product_boxes = []
    for i, box in enumerate(yolo_results.boxes.xyxy):
        box = np.array(box) * scaling_factor
        label = yolo_results.names[int(yolo_results.boxes.cls[i])]
        box_resized = {'class': label, 'box': box}

        # Split the boxes into two lists.
        if label == 'nameItem' or label == 'priceItem':
            product_boxes.append(box_resized)
        else:
            info_boxes.append(box_resized)

    # ocr_img = cv2.imread("Images/transformed/" + img_name)
    ocr_img = transformed_image
    # ocr_img = cv2.imdecode(np.frombuffer(yolo_results.imgs[0], np.uint8), cv2.IMREAD_COLOR)
    height, width, _ = yolo_img_resized.shape
    ocr_img_resized = cv2.resize(ocr_img, (width, height))

    return yolo_img_resized, ocr_img_resized, product_boxes, info_boxes
