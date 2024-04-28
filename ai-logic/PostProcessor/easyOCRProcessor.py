import logging
from PIL import ImageDraw, Image

'''
This class is not used in the current version of the project.
It was an attempt to use the easyOCR library to process the images.
But after some testing it was clear that the library is not suitable for the project.

Compared to Tesseract, the OCR results on bad images were better.
But as soon as the images had a reasonable quality, the results were way worse than Tesseract.

It's code is kept in the project to show the attempt to use it.
'''

'''
def init_ocr_engine(lang='de', model='craft'):
    logging.info("Initializing OCR engine")
    try:
        ocr_engine = easyocr.Reader([lang])
        #ocr_engine = easyocr.Reader([lang], detect_network="dbnet18")
    except:
        raise Exception("Could not initialize OCR engine.")
    return ocr_engine
'''


def read_text(ocr_engine, image):
    logging.info("Reading text from image")
    try:
        # image = image.astype(np.float32)  # convert image data to float32
        return ocr_engine.readtext(image, detail=1,
                                   allowlist='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-. ,')
    except:
        raise Exception("Could not read text from image.")


def create_bounding_boxes(ocr_engine, image):
    logging.info("Creating bounding boxes")
    try:
        boxes = ocr_engine.detect(image)

        return boxes
    except:
        raise Exception("Could not create bounding boxes.")


def draw_ocr_boxes(boxes, image, image_name):
    image = Image.open(image)
    draw = ImageDraw.Draw(image)
    for box in boxes[0][0]:  # Flatten the list
        x_min, x_max, y_min, y_max = sorted(box[0:2]) + sorted(box[2:4])  # Sort the x and y values
        draw.rectangle([x_min, y_min, x_max, y_max], outline="blue", width=2)
    # image.save(f'TestImages/output/{image_name}_boxes.jpg')  # Save the image to a file
    # image.show()  # Display the image
