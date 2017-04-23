# Fixed SchoolMate application

This folder contains the fixed SchoolMate application.

## Run

You can run it using Docker Compose:
```bash
docker-compose build
docker-compose up
```

Docker Compose will build and run 2 containers, one running PHP to serve the application,
the second running MySQL. The MySQL database is initialized using the `SchoolMate.sql` script.
Please note that the SQL script has been slightly adapted to run on MySQL 5.7.

The application will be available on port `2002`. The default credentials are `test:test`.
If you need to access the database, you can access it on port `3308` using the credentials `root:password`.

## Stop
To stop the application, simply press Ctrl+C.

## Clean
To start with a fresh application, simply run:
```bash
docker-compose rm -f
docker-compose build
docker-compose up
```
