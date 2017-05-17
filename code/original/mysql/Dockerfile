FROM mysql:5.5

# prepare the database
COPY sql/01_SchoolMate.sql /docker-entrypoint-initdb.d/01_SchoolMate.sql
COPY sql/02_user.sql /docker-entrypoint-initdb.d/02_user.sql
COPY sql/03_setup.sql /docker-entrypoint-initdb.d/03_setup.sql
