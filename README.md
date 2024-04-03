This project is a university software project. My task was to

* Build the backend logic with Java and the database with PostgreSQL
* Dockerize the frontend, backend, database and write a docker-compose.yml for all services
* Integrate the login service from Auth0 by Okta.

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
