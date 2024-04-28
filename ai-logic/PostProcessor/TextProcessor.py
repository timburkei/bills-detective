from dateutil import parser
import re

'''
This file holds the functions for the postprocessing of the text that was extracted from the images.
'''


# Define the labels for the different types of text
digit_labels = ['invoiceField', 'numberStore', 'zipStore', 'totalAmount', 'taxes7Amount', 'taxes19Amount',
                'discountsAmount', 'priceItem']
letter_labels = ['cityStore', 'nameStore', 'nameItem']
time_labels = ['invoiceTime']
date_labels = ['invoiceDate']
address_labels = ['streetStore']

def postprocess_text(text, label):
    '''
    This function is mainly responsible for postprocessing the provided text that was extracted from the images.
    :param text: The text that was extracted from the image
    :param label: The datatype label of the text, to match the regex rules
    :return:
    '''

    # Define the regex patterns for the different types of text
    digit_regex = r"[^0-9.,]"
    letter_regex = r"[^a-zA-Zäüßöé ]"
    time_regex = r"[^0-9:]"
    date_regex = r"[^0-9.]"
    address_regex = r"[^a-zA-Z0-9-.,]"
    general_regex = r"[^a-zA-Z0-9-.,]"

    # Process the text for digits
    if label in digit_labels:
        # Apply the digit regex pattern to the text
        text = re.sub(digit_regex, "", text)

        # Replace the comma with a dot
        text = text.replace(",", ".")

        # Remove leading and trailing spaces
        text = text.rstrip(".")

        if label in ['totalAmount', 'taxes7Amount', 'taxes19Amount', 'discountsAmount', 'priceItem']:

            # If the text consists of 3 digits (possibly negative)
            if re.match(r"^-?\d{3}$", text):
                # Add a "." after the first digit
                text = re.sub(r"^(-?\d)(\d{2})$", r"\1.\2", text)
            # If the text consists of 4 digits (possibly negative)
            elif re.match(r"^-?\d{4}$", text):
                # Add a "." after the second digit
                text = re.sub(r"^(-?\d{2})(\d{2})$", r"\1.\2", text)

            # If the text consists of 5 digits (possibly negative)
            text = re.sub('\.{2,}', '.', text)

            # Finally check if the text is a float, else return defualt value 1.00
            text = check_float(text, label)

        if label == 'zipStore':
            text = check_zip_store(text)

    elif label in letter_labels:
        # Replace german special characters
        text = text.replace("ä", "ae")
        text = text.replace("ö", "oe")
        text = text.replace("ü", "ue")
        text = text.replace("ß", "ss")
        text = text.replace("é", "e")

        # Use re.sub to replace the pattern with the first letter, a space, and the second letter
        text = re.sub("([a-zA-Z])[.,-]([a-zA-Z])", r"\1 \2", text)

        # Define the regex pattern for a number followed by a single letter
        text = re.sub(r"(\d+)[a-zA-Z]", "", text)

        # Clean text based on basic regex for letters
        text = re.sub(letter_regex, "", text)

        # Remove leading and trailing spaces
        text = text.strip()

        if label == 'cityStore':
            text = prevent_spaces_in_city(text)

        text = text.upper()

    elif label in time_labels:
        text = re.sub(time_regex, "", text)
        text = standardize_time(text)
    elif label in date_labels:
        text = re.sub(date_regex, "", text)
        text = standardize_date(text)
    elif label in address_labels:
        text = re.sub(address_regex, "", text)
    else:
        text = re.sub(general_regex, "", text)

    # Remove multiple spaces
    if not isinstance(text, float):
        text = re.sub(' {2,}', ' ', text)

    return text


def standardize_date(date):
    '''
    This function standardizes the date into the format DD.MM.YYYY
    If an error occurs, the function returns the default date 01.01.2024
    :param date: the string representation of the date
    :return:
    '''
    try:
        # Parse the date
        date = parser.parse(date)

        # Format the date into the desired format (DD.MM.YY)
        standardized_date = date.strftime('%d.%m.%Y')
    except:
        standardized_date = "01.01.2024"
    return standardized_date


def standardize_time(time):
    '''
    This function standardizes the time into the format HH:MM:SS
    :param time: the time as a string
    :return: the standardized time as a string
    '''
    try:
        # Parse the time
        time = parser.parse(time)

        # Format the time into the desired format (HH:MM:SS)
        standardized_time = time.strftime('%H:%M:%S')
    except:
        standardized_time = "01:00:00"

    return standardized_time


def prevent_spaces_in_city(text):
    # The regular expression pattern for a word of length less than 5, a space, and another word of length less than 5
    pattern = r"(\b\w{1,4}\s|\s\w{1,4}\b)"

    # Find all matches of the pattern in the text
    matches = re.findall(pattern, text)

    # For each match, remove the space
    for match in matches:
        text = text.replace(match, match.replace(" ", ""))

    return text


def check_zip_store(text):
    '''
    This function checks if the provided text is a valid zip code.
    Else the default zip code 71364 is returned.
    :param text:
    :return:
    '''
    # Check if the text has exactly 5 digits
    if re.fullmatch(r'\d{5}', text):
        return text
    return "71364"

def check_float(text, label):
    '''
    This function checks if the provided text is a valid float.
    Else the default value 1.00 is returned.
    :param text:
    :param label:
    :return:
    '''
    try:
        text_len = len(text.replace(".", ""))
        price = float(text)
        if price >= 10 and label == 'priceItem' and text_len == 2:
            price = price / 100
        elif label == 'priceItem' and text_len == 1:
            price = price / 10
        return price
    except:
        return 1.00
