# Introduction
This project is a university software project. My task was to

* Build the backend logic with Java and the database with PostgreSQL
* Dockerize the frontend, backend, database and write a docker-compose.yml for all services
* Integrate the login service from Auth0 by Okta.

Our common goal for this project was to implement a user-friendly application that uses AI to sort uploaded receipts and display them in easy-to-understand graphics.

Our project aims to assist users in visualising and monitoring their grocery spending in a simple and enjoyable manner. By scanning receipts, our AI components and graphics enable users to track their spending, monitor price changes, and shopping behaviour.

This helps users to gain a better understanding of inflation in Germany. Uploading receipts is a breeze, and with minimal data, users can obtain a comprehensive overview of their shopping behaviour. For instance, individuals can monitor the price changes of different products, such as bananas, over time and determine the most affordable place to make a purchase.

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
