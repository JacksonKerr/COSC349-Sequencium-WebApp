<?php
//Setup shell variables

$db_host = '192.168.5.4';
$db_name = 'scoreHold';
$db_user = 'webuser';
$db_passwd = 'insecure_db_pw';

$pdo_dsn = "mysql:host=$db_host;dbname=$db_name";

$pdo = new PDO($pdo_dsn, $db_user, $db_passwd);

//sql query
$q = $pdo->query("SELECT * FROM scores");

//echo database statements
while ($row = $q->fetch()){
      //Needs to print out as html TABLE. 
     echo$row["username"]." ".$row["score"]." ";
}

?>