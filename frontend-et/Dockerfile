# Stage 1: Build the Angular application
FROM node:18-alpine AS build

# Set the working directory
WORKDIR /app

RUN npm cache clean --force

RUN npm cache clean --force

# Copy package.json and package-lock.json
COPY package.json package-lock.json ./

RUN npm cache clean --force

# Install Angular CLI globally
RUN npm install -g @angular/cli

# Install project dependencies
RUN npm install

# Copy the project files into the Docker image
COPY . .

# Build the Angular app in production mode
RUN npm run build

# Stage 2: Serve the Angular application using Nginx
FROM nginx:alpine

# Copy the built app from the build stage
COPY --from=build /app/dist/* /usr/share/nginx/html/

# Remove the default Nginx configuration file
RUN rm /etc/nginx/conf.d/default.conf

# Copy a new configuration file from the current directory
COPY nginx.conf /etc/nginx/conf.d

# Expose port 4200
EXPOSE 4200

# Start Nginx when the container has provisioned
CMD ["nginx", "-g", "daemon off;"]
