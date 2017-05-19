# Schoolmate - Database
This folder contains the SQL scripts used to setup the testing environment,
as well as a Dockerfile that can be used to create a MySQL Docker image

## SQL Scripts
The scripts are contained in the [sql](sql) folder.
The following table describes their functions.

| Name              | Description                                                                                  |
|-------------------|----------------------------------------------------------------------------------------------|
| 01_Schoolmate.sql | Original SQL script included in SchoolMate. Create the `schoolmate` database and the schema. |
| 02_user.sql       | Creates the user `schoolmate` and grants all privileges on the `schoolmate` database.        |
| 03_setup.sql      | Add some minimal content to the application to make it immediately testable.                 | 
