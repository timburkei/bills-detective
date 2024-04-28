# Use an official Python runtime as a parent image
FROM python:3.11-slim-bullseye

# Set the working directory in the container to /app
WORKDIR /app

# Install gcc, python3-dev, and libgl1-mesa-dev
RUN apt-get update && apt-get install -y gcc python3-dev libgl1-mesa-dev

# Add Tesseract
RUN apt-get update \
    && apt-get install -y tesseract-ocr \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create an empty image directory
RUN mkdir -p /app/Images

# Copy requirements.txt and install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy the current directory contents into the container at /app
COPY . .

CMD if [ ! -f ".env" ]; then touch .env && python SecurityGuard/KeyGenerator.py; fi && gunicorn -w 1 -k uvicorn.workers.UvicornWorker --timeout 90 -b 0.0.0.0:8000 main:app
#CMD if [ ! -f ".env" ]; then touch .env && python SecurityGuard/KeyGenerator.py; fi && gunicorn -w 1 -k uvicorn.workers.UvicornWorker --timeout 90 -b 0.0.0.0:8000 --access-logfile gunicorn_access.log --error-logfile gunicorn_error.log main:app