import cv2
import pytesseract
from PostProcessor.TextProcessor import postprocess_text

'''
This class holds the configuration and functions for the pytesseract OCR Engine.
'''

# Set the data type for each label
digit_labels = ['invoiceField', 'numberStore', 'zipStore', 'totalAmount', 'taxes7Amount', 'taxes19Amount',
                'discountsAmount', 'priceItem']
letter_labels = ['cityStore', 'nameStore', 'nameItem']
time_labels = ['invoiceTime']
date_labels = ['invoiceDate']
address_labels = ['streetStore']

# Turns the image to grayscale and returns it
def optimize_image_for_pytesseract(image):
    # Convert the image to grayscale
    image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    return image


def map_label_to_tesseract(label):
    '''
    This function maps the label to the correct configuration for the pytesseract OCR Engine.
    Each data type has a different configuration to optimize the OCR results.
    :param label:
    :return:
    '''
    oem = 3  # 0    Legacy engine only.
    # 1    Neural nets LSTM engine only.
    # 2    Legacy + LSTM engines.
    # 3    Default, based on what is available.
    psm = 6
    if label in digit_labels:
        config = f'--oem {oem} --psm {psm} tessedit_char_whitelist=0123456789.,-'
    elif label in letter_labels:
        config = f"-l eng+deu --oem {oem} --psm {psm} tessedit_char_whitelist=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-"
    elif label in time_labels:
        config = f'--oem {oem} --psm {psm} tessedit_char_whitelist=0123456789:'
    elif label in date_labels:
        config = f'--oem {oem} --psm {psm} tessedit_char_whitelist=0123456789.'
    elif label in address_labels:
        config = f'--oem {oem} --psm {psm} tessedit_char_whitelist=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.,'
    else:
        config = f"-l eng+deu --oem {oem} --psm {psm}"

    return config


def pytesseract_read_text(image, label='general'):
    '''
    This function runs the pytesseract OCR Engine on the image and returns the recognized text.
    Within the process, the text is postprocessed to correct common OCR errors.
    :param image:
    :param label:
    :return:
    '''
    config = map_label_to_tesseract(label)

    # Run tesseract OCR on the image
    text = pytesseract.image_to_string(image, config=config)

    corrected = postprocess_text(text, label)

    return corrected
