import cv2


def optimize_image_for_ocr(image, image_name, black_text_on_white=False):
    '''
    This function optimizes the image for OCR by applying a gaussian blur and a morphological close operation.
    This reduces noise and makes the text more readable for the OCR.
    It also repairs broken letters and lines by applying a morphological close operation.
    :param image: The image that will be read by the ocr
    :param image_name: the name of the image
    :param black_text_on_white: boolean, that if true inverts the image from black text on white background to white text on black background
    :return:
    '''
    blur = cv2.GaussianBlur(image, (3, 3), 0)

    if black_text_on_white:
        blur = cv2.bitwise_not(blur)

    # Best solution so far
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1, 1))
    optimized = cv2.morphologyEx(blur, cv2.MORPH_CLOSE, kernel, iterations=2)

    #cv2.imwrite(f"Images/ocr/{image_name}", optimized)
    return optimized
