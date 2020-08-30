#! /bin/bash
# DATABASE CONFIGURATION SHOULD GO HERE

# Update Ubuntu software packages.
apt-get update

# We create a shell variable MYSQL_PWD that contains the MySQL root password
export MYSQL_PWD='insecure_mysqlroot_pw'

echo "mysql-server mysql-server/root_password password $MYSQL_PWD" | debconf-set-selections 
echo "mysql-server mysql-server/root_password_again password $MYSQL_PWD" | debconf-set-selections

# Install the MySQL database server.
apt-get -y install mysql-server

# First create a database.
echo "CREATE DATABASE scoreDB;" | mysql

# Then create a database user "webuser" with the given password.
echo "CREATE USER 'webuser'@'%' IDENTIFIED BY 'insecure_db_pw';" | mysql

echo "GRANT ALL PRIVILEGES ON scoreDB.* TO 'webuser'@'%'" | mysql

# Set the MYSQL_PWD shell variable that the mysql command will
# try to use as the database password ...
export MYSQL_PWD='insecure_db_pw'

cat /vagrant/scoreDB.sql | mysql -u webuser scoreDB

sed -i'' -e '/bind-address/s/127.0.0.1/0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf

# We then restart the MySQL server to ensure that it picks up
# our configuration changes.
service mysql restart

# Maybe have a look at the database server from the labs.
