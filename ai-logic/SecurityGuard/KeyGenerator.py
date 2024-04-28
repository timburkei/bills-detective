import os
from dotenv import load_dotenv, set_key
import secrets

'''
This class creates and manages the API Key, that is used to authenticate to the FastAPI application.
It is a singleton class, so only one instance of the class can be created.

'''

class KeyGenerator:
    _instance = None

    # Singleton pattern
    def __new__(cls, *args, **kwargs):
        if not isinstance(cls._instance, cls):
            cls._instance = super(KeyGenerator, cls).__new__(cls, *args, **kwargs)
        return cls._instance

    def __init__(self):
        self.key = None
        # self.backend_key = None
        self.env_path = None
        self._set_key()


    def _generate_new_key(self):
        new_key = secrets.token_urlsafe(64)
        if os.path.isfile(self.env_path):
            set_key(self.env_path, "API_KEY", new_key)
            self._set_key()

        return self.key

    def get_key(self):
        return self.key

    # def get_backend_key(self):
    #     return self.backend_key

    def _set_key(self):
        load_dotenv(self.env_path)
        self.key = os.getenv('API_KEY')
        # self.backend_key = os.getenv('BACKEND_KEY')

    def set_env_path(self, env_path):
        self.env_path = os.path.abspath(env_path)


if __name__ == "__main__":
    key = KeyGenerator()
    key.set_env_path(".env")
    key._generate_new_key()
    print("---------------------------------------------------------------------------------------------")
    print("---------------------------------------------------------------------------------------------")
    print("This is application API Key:")
    print(key.get_key())
    print("---------------------------------------------------------------------------------------------")
    print("---------------------------------------------------------------------------------------------")

