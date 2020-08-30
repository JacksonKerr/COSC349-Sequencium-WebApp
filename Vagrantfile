# -*- mode: ruby -*-
# vi: set ft=ruby :

# A Vagrantfile to set up two VMs, a webserver and a database server,
# connected together using an internal network with manually-assigned
# IP addresses for the VMs.

Vagrant.configure("2") do |config|
  # (We have used this box previously, so reusing it here should save a
  # bit of time by using a cached copy.)
  config.vm.box = "ubuntu/xenial64"

  # Webserver which will serve the webpage used to interact with the 
  # sequencium agent and highscore database
  config.vm.define "webserver" do |webserver|
    webserver.vm.hostname = "webserver"

    # Allow host to connect to 127.0.0.1:8080 to reach webserver VM port 80.
    webserver.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"
    
    # We set up a private network that our VMs will use
    webserver.vm.network "private_network", ip: "192.168.5.2"

    # Mystery line we have to include when working in the lab
    webserver.vm.synced_folder "./webserver", "/vagrant", owner: "vagrant", group: "vagrant", mount_options: ["dmode=775,fmode=777"]

    # Runs a script when the webserver is created.
    webserver.vm.provision "shell", path: "webserver/webserver.sh"
  end

  # Opposition agent server
  config.vm.define "seqAgent" do |seqAgent|
    seqAgent.vm.hostname = "seqAgent"

    # Giving VM an address on the private network
    seqAgent.vm.network "private_network", ip: "192.168.5.3"

    # Mount shared folder.
    seqAgent.vm.synced_folder "./seqAgent", "/vagrant", owner: "vagrant", group: "vagrant", mount_options: ["dmode=775,fmode=777"]
    
    seqAgent.vm.provision "shell", path: "seqAgent/seqAgent.sh"
  end

  # Highscore Databse Server
   config.vm.define "scoreDB" do |scoreDB|
     scoreDB.vm.hostname = "scoreDB"

     # Giving VM an address on the private network
     scoreDB.vm.network "private_network", ip: "192.168.5.4"

     # Mount shared folder.
     scoreDB.vm.synced_folder "./scoreDB", "/vagrant", owner: "vagrant", group: "vagrant", mount_options: ["dmode=775,fmode=777"]
    
     scoreDB.vm.provision "shell", path: "scoreDB/scoreDB.sh"
   end

end

#  LocalWords:  webserver xenial64
