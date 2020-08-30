#! /bin/bash

# TODO don't need compiler when project is done
sudo apt install openjdk-8-jdk

# Install jre
sudo apt-get install openjdk-8-jre -y


# TODO don't need to compile when project is done
sudo apt install openjdk-8-jdk -y
javac /vagrant/sequencium/*.java
java -cp /vagrant seqplayer.seqServer &
