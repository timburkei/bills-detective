import functools
import logging
import time
from typing import Any

from Utils.SingletonHelper import Singleton

class Logger(metaclass=Singleton):

    def __init__(self, name=None):
        self.logger = logging.getLogger(name)
        if not self.logger.handlers:
            handler = logging.StreamHandler()
            # Change the time format to hh:mm:ss
            handler.setFormatter(logging.Formatter('%(asctime)s - [%(levelname)s] - %(message)s', "%H:%M:%S"))
            self.logger.addHandler(handler)
            self.logger.setLevel(logging.INFO)

    # Rest of your code...

def log_func_call(logger):
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            # Start timer
            start_time = time.time()

            # logger.logger.info(f"[{func.__name__}] - CALLING")
            result = func(*args, **kwargs)

            # End timer
            end_time = time.time()
            processing_time = end_time - start_time

            # Log the result and processing time together
            logger.logger.error(f"[{func.__name__}] - RETURNING - {result} - PROCESSING TIME - {processing_time}")
            return result

        return wrapper

    return decorator


def log_exception(logger: Any) -> Any:
    def decorator(func: Any) -> Any:
        @functools.wraps(func)
        def wrapper(*args: Any, **kwargs: Any) -> Any:
            try:
                return func(*args, **kwargs)
            except Exception as e:
                logger.error(f"[{func.__name__}] - MESSAGE - {str(e)}")
                logger.error(f"[{func.__name__}] - VALUES - {kwargs}")
                raise

        return wrapper

    return decorator