import cv2
from PIL import Image, ImageDraw

'''
This class has the purpose to generate lines out of the bounding boxes from the Yolo Detection.
It is used to group the bounding boxes related to name and price of a product.
'''

def extract_yolo_lines_2(boxes):
    '''
    This function groups the bounding boxes into lines.
    For that is first sorts the boxes by the y_min value.
    With this boxes that are corresponding to each other are ordered in the list.
    Then it calculates the average height of the boxes and groups the boxes into lines.
    This calculation serves as a threshold to group the boxes into lines.
    So even when the boxes are not perfectly aligned, they are still grouped into the same line.
    :param boxes: The predicted bounding boxes from the Yolo model
    :return: lines of boxes
    '''
    # Sort boxes by y_min
    boxes = boxes[0]
    boxes.sort(key=lambda box: box['box'][1].item())

    try:
        # Calculate the height and width of each box
        heights = [box['box'][3].item() - box['box'][1].item() for box in boxes]

        # Compute the average height and width
        avg_height = sum(heights) / len(heights)

        # Exclude boxes that are more than twice the average height or width
        boxes = [box for box in boxes if (box['box'][3].item() - box['box'][1].item()) <= 2 * avg_height]

        # Set the threshold to 50% of the average height
        threshold = 0.5 * avg_height
    except:
        threshold = 12

    if threshold > 40:
        threshold = 20

    if threshold < 10:
        threshold = 10

    lines = []
    current_line = [boxes[0]]

    # Group boxes into lines
    for box in boxes[1:]:
        # If the y_min of the box is close to the y_min of the current line, add it to the current line
        if abs(box['box'][1] - current_line[0]['box'][1]) < threshold or abs(
                box['box'][3] - current_line[0]['box'][3]) < threshold:  # Set the threshold according to your needs
            current_line.append(box)
        else:
            # Sort the boxes in the current line by x_min
            current_line.sort(key=lambda box: box['box'][0])
            lines.append(current_line)
            # lines.append({"line": [], "boxes": current_line})
            current_line = [box]

    # Don't forget to add the last line
    current_line.sort(key=lambda box: box['box'][0])
    # lines.append({"line": [], "boxes": current_line})
    lines.append(current_line)

    for i, line in enumerate(lines):
        if len(line) > 2:
            lines[i] = merge_boxes_in_line(line)

    return lines


def merge_boxes_in_line(line):
    '''
    This function merges the boxes of a line into two boxes.
    Sometimes the model predicts several boxes for one element, that are then overlapping each other.
    To only have one box for the name and one for the price, the boxes with the same labels are merged into one boxes.
    :param line: Takes in a line of boxes
    :return: Returns a line of maximum two boxes
    '''
    classes = ['nameItem', 'priceItem']
    merged_boxes = []

    for cls in classes:
        # Filter boxes of the current class
        class_boxes = [box for box in line if box['class'] == cls]

        if class_boxes:
            # Find the minimum and maximum x and y coordinates among the boxes of the current class
            x_min = min(box['box'][0].item() for box in class_boxes)
            y_min = min(box['box'][1].item() for box in class_boxes)
            x_max = max(box['box'][2].item() for box in class_boxes)
            y_max = max(box['box'][3].item() for box in class_boxes)

            # Create the merged box
            merged_box = {'class': cls, 'box': [x_min, y_min, x_max, y_max]}
            merged_boxes.append(merged_box)

    return merged_boxes


def calculate_distance(current_box, match_box, ):
    '''
    This helper function calculates the distance between two boxes.
    :param current_box: A predicted box from the Yolo model
    :param match_box: A possbile matching box
    :return:
    '''
    box_height = abs(current_box[3] - current_box[1])
    box_middle_y = current_box[1] + (box_height / 2)

    match_box_height = abs(match_box[3] - match_box[1])
    middle_y = (match_box[1] + (match_box_height / 2))

    return abs(box_middle_y - middle_y)


def find_missing_informations(lines, avg_height):
    '''
    This function tries to find missing information in the lines.
    When the model does not predict a box for the name or the price of a product,
    this function tries to find the missing information in the previous or next line.
    Due to the thresshold, related information can be cut off from the line.

    As of now the function is not finished and therefore not used in the current version of the project.
    It is kept in the project to show future improvements.
    :param lines: Takes in a line of boxes
    :param avg_height: the average height of the boxes as threshold
    :return:
    '''
    corrected_lines = []
    for i, line in enumerate(lines):
        if i > 1 and len(line) > 1:
            corrected_lines.append(line)
        else:
            current_label = line[0]['class']
            current_box = line[0]['box']
            previous_line = lines[i - 1]
            next_line = lines[i + 1]

            distance_previous = None
            if len(previous_line) < 2 and current_label != previous_line[0]['class']:
                previous_box = previous_line[0]['box']
                distance_previous = calculate_distance(current_box=current_box,
                                                       match_box=previous_box)

            distance_next = None
            if len(next_line) < 2 and current_label != next_line[0]['class']:
                next_box = next_line[0]['box']
                distance_next = calculate_distance(current_box=current_box,
                                                   match_box=next_box)

            if not distance_previous or distance_previous > (avg_height * 0.9):
                distance_previous = None
            if not distance_next or distance_next > (avg_height * 0.9):
                distance_next = None

            if distance_previous and distance_next:
                min_value = min(distance_previous, distance_next)

                if min_value == distance_previous:
                    corrected_lines.append([line[0], previous_line[0]])
                else:
                    corrected_lines.append([line[0], next_line[0]])
            elif distance_previous:
                corrected_lines.append([line[0], previous_line[0]])
            elif distance_next:
                corrected_lines.append([line[0], next_line[0]])
            else:
                corrected_lines.append(line)


def draw_yolo_line_boxes(lines, image, image_name):
    '''
    This function draws the lines on the image for visualization and analysis.
    :param lines: The lines from extract_yolo_lines_2
    :param image: the image to draw the lines on
    :param image_name: The name of the image
    :return:
    '''

    for line in lines:
        # Sort the boxes in the line by y-coordinate, then by x-coordinate
        line.sort(key=lambda box: box['box'][0])
        # Calculate the minimum and maximum x and y coordinates for the line
        x_min = min(int(box['box'][0]) for box in line)
        y_min = min(int(box['box'][1]) for box in line)
        x_max = max(int(box['box'][2]) for box in line)
        y_max = max(int(box['box'][3]) for box in line)
        # Draw a rectangle around the line using OpenCV
        cv2.rectangle(image, (x_min, y_min), (x_max, y_max), (0, 0, 255), 1)
    return image
