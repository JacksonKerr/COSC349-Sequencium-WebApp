<?php
$db_host   = '192.168.5.4';
$db_name   = 'scoreHold';
$db_user   = 'webuser';
$db_passwd = 'insecure_db_pw';
$board_name = $_GET['name'];
$board_score = $_GET['score'];

echo $board_name;
echo $board_score;

$pdo_dsn = "mysql:host=$db_host;dbname=$db_name";

$pdo = new PDO($pdo_dsn, $db_user, $db_passwd);
$pdo->query("DELETE FROM scores WHERE username = '$board_name' AND score < $board_score");
$q = $pdo->query("INSERT INTO scores VALUES ('$board_name', $board_score)");
echo $q;
?>