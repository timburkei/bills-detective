# run project
Make sure that you have the valid .env file!

To start the Docker image, run the following command in the root directory of the project:
```bash
docker-compose up
```

# Swagger UI
Link to Swagger UI
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Hint category
tag = category<br>
product = subcategory

# Development on a Docker Container
```bash
docker-compose -f docker-compose.dev.yml up --build
```
