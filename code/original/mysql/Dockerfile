FROM mysql

# prepare the database
COPY 01_SchoolMate.sql /docker-entrypoint-initdb.d/01_SchoolMate.sql
COPY 02_user.sql /docker-entrypoint-initdb.d/02_user.sql
