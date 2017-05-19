# SchoolMate - original version
This folder contains the original vulnerable SchoolMate application.

## Run
You can run the application using Docker Compose:
```bash
docker-compose build
docker-compose up
```

Docker Compose will build and run 2 containers, one running PHP to serve the application, the second running MySQL.
The MySQL database is properly initialized with the scripts contained in the [database](../database) folder.

The application will be available on port `2001`.
The default credentials for the admin user are `test:test`.
If you need to access the database, you can access it on port `3307` using the credentials `root:password`.

## Stop
To stop the application, simply press Ctrl+C.

## Clean
To start with a fresh application, simply run the following commands:
```bash
docker-compose rm -f
docker-compose build
docker-compose up
```
This will destroy the existing Docker containers, re-build the images and start the containers again.
