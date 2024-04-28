# ai-logic-et

## Setting up the project as standalone application

**_This is a guid on how to setup the project as a standalone application. 
<br>
<br>
Some functions are not available in the standalone version because of the missing connection the backend._**
<br>
<br>

Activate the virtual environment and install the dependencies

1. `python -m venv venv`
2. `source venv/bin/activate`
3. `pip install -r requirements.txt`

Create a .env file in the SecurityGuard folder and generate a key

4. `touch SecurityGuard/.env`<br>
5. `python SecurityGuard/KeyGenerator.py`

Start the server

6. `uvicorn main:app --reload`


## Make a post request
Here is example on how to perform a request with cURL
Please replace `<your-api-key>` with the key generated in the .env file
Also change the path to the image you want to upload

`
 ``curl --location 'localhost:8000/upload'
--header 'X-API-Key: <your-api-key>'
--form 'file=@"Testing/images/IMG_0001.jpeg"``

## Links
The application will be available on localhost:8000

The Swagger documentation will be available on 
http://localhost:8000/docs

The Admin panel will be available on
http://localhost:8000/admin

Without the backend connection, the admin panel will not be able to upload images.

