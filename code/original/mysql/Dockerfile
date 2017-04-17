FROM mysql
#FROM mysql:5.5

# prepare the database
COPY 01_SchoolMate.sql /docker-entrypoint-initdb.d/01_SchoolMate.sql
COPY 02_user.sql /docker-entrypoint-initdb.d/02_user.sql
COPY 03_setup.sql /docker-entrypoint-initdb.d/03_setup.sql
