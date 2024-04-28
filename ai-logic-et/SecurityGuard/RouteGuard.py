
from fastapi import HTTPException, Security
from fastapi.security import APIKeyHeader
from SecurityGuard.KeyGenerator import KeyGenerator

# from main import ENV_PATH


'''
This file contains the security logic for the FastAPI application.
The function is injected into the routes to secure the routes with an API Key.
It awaits the API Key from the request and compares it to the API Key that is stored in the .env file.
'''
api_key_header = APIKeyHeader(name="X-API-Key", auto_error=False)


def api_key_auth(provided_api_key: str = Security(api_key_header)):
    '''
    A security function to secure the routes with an API Key.
    It compares the provided API Key with the API Key that is stored in the .env file.
    :param provided_api_key: The API Key that is provided in the request.
    :return:
    '''
    key = KeyGenerator()
    api_key = key.get_key()

    if provided_api_key == api_key:
        return provided_api_key
    else:
        raise HTTPException(status_code=401, detail="Invalid API Key")


if __name__ == "__main__":
    api_key_auth(provided_api_key="test")
