FROM php:5-alpine

# install needed packages to run the application
RUN docker-php-ext-install mysql
RUN docker-php-ext-enable mysql

# add the application
ADD schoolmate /schoolmate

# entry point
EXPOSE 8000
ENTRYPOINT ["php", "-S", "0.0.0.0:8000", "-t", "/schoolmate"]
