<?php
$db_host   = '192.168.5.4';
$db_name   = 'scoreDB';
$db_user   = 'webuser';
$db_passwd = 'insecure_db_pw';
$board_name = $_GET['name'];
$board_score = $GET['score'];

$pdo_dsn = "mysql:host=$db_host;dbname=$db_name";

$pdo = new PDO($pdo_dsn, $db_user, $db_passwd);

$q = $pdo->query("INSERT INTO score VALUES ('$board_name', $score)");

?>