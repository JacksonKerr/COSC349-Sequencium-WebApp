#! /bin/bash

# Install jdk
sudo apt-get update -y
sudo apt-get install openjdk-8-jre -y
sudo apt install openjdk-8-jdk -y

# Compile and run server
javac /vagrant/seqplayer/*.java
java -cp /vagrant seqplayer.seqServer &
