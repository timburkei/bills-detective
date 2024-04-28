import cv2

'''
This class is not used in the current version of the project.
It was an attempt to deskew the images before running the OCR on them.
The deskewing was not successful and the class was not used in the final version of the project.

In a later version this can help to improve the OCR results.
Because Tesseract has a hard time reading text that is not horizontal.
Even small angles can lead to bad results.
'''
def get_normalized_image(path):
    img = cv2.imread(path)
    resized_height = 2560
    percent = resized_height / len(img)
    resized_width = int(percent * len(img[0]))
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (3, 3), 0)
    cv2.imwrite("/Users/ericbernet/Desktop/01_Studium/01_HDM/01_Vorlesungen/06_Semester/MediaNightProjekt/Deskewing/blur.jpg", gray)
    gray = cv2.resize(gray, (resized_width, resized_height))
    cv2.imwrite("/Users/ericbernet/Desktop/01_Studium/01_HDM/01_Vorlesungen/06_Semester/MediaNightProjekt/Deskewing/resized.jpg", gray)
    try:
        start_point = (0, 0)
        end_point = (gray.shape[0], gray.shape[1])
        color = (255, 255, 255)
        thickness = 10
        gray = cv2.rectangle(gray, start_point, end_point, color, thickness)
        cv2.imwrite("/Users/ericbernet/Desktop/01_Studium/01_HDM/01_Vorlesungen/06_Semester/MediaNightProjekt/Deskewing/cropped.jpg", gray)
    except:
        print("Failed to crop border")
    gray = cv2.bitwise_not(gray)
    cv2.imwrite("/Users/ericbernet/Desktop/01_Studium/01_HDM/01_Vorlesungen/06_Semester/MediaNightProjekt/Deskewing/inverted.jpg", gray)

    return gray


def get_skew_angle(gray):
    thresh = cv2.threshold(gray, 0, 255,
                           cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]
    kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (30, 5))
    cv2.imwrite("/Users/ericbernet/Desktop/01_Studium/01_HDM/01_Vorlesungen/06_Semester/MediaNightProjekt/Deskewing/thresh.jpg", thresh)
    dilate = cv2.dilate(thresh, kernel)
    contours, hierarchy = cv2.findContours(dilate, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

    angles = []
    for contour in contours:
        minAreaRect = cv2.minAreaRect(contour)
        angle = minAreaRect[-1]
        if angle != 90.0 and angle != -0.0:
            angles.append(angle)

    angles.sort()
    mid_angle = angles[int(len(angles) / 2)]
    print(angles)
    print(mid_angle)
    cv2.namedWindow('dilate',cv2.WINDOW_NORMAL)
    cv2.imshow("dilate", dilate)
    cv2.imwrite("/Users/ericbernet/Desktop/01_Studium/01_HDM/01_Vorlesungen/06_Semester/MediaNightProjekt/Deskewing/dilate.jpg", dilate)
    return mid_angle


def deskew(path):
    original = cv2.imread(path)
    img = get_normalized_image(path)
    angle = get_skew_angle(img)
    # angle = np.rad2deg(angle)
    print(angle)
    if angle > 45:  # anti-clockwise
        angle = -(90 - angle)
    height = original.shape[0]
    width = original.shape[1]
    m = cv2.getRotationMatrix2D((width / 2, height / 2), angle, 1)

    # deskewed = cv2.warpAffine(original, m, (width, height), borderMode=cv2.BORDER_REPLICATE)
    deskewed = cv2.warpAffine(original, m, (width, height), borderValue=(255, 255, 255))
    # cv2.namedWindow('deskewed',cv2.WINDOW_NORMAL)
    # cv2.imshow("deskewed", deskewed)
    # cv2.namedWindow('original',cv2.WINDOW_NORMAL)
    # cv2.imshow("original", original)
    # cv2.waitKey(0)
    return deskewed
