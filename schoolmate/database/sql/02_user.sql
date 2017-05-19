CREATE USER 'schoolmate'@'%' IDENTIFIED BY 'schoolmate';
GRANT ALL ON schoolmate.* TO 'schoolmate'@'%';
FLUSH PRIVILEGES;
