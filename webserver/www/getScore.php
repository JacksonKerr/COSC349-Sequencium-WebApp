<?php
//Setup shell variables

$db_host = '192.168.5.4';
$db_name = 'scoreHold';
$db_user = 'webuser';
$db_passwd = 'insecure_db_pw';

$pdo_dsn = "mysql:host=$db_host;dbname=$db_name";

$pdo = new PDO($pdo_dsn, $db_user, $db_passwd);

//sql query
$q = $pdo->query("SELECT * FROM scores ORDER BY score DESC");

//echo database statements in a tble, showing data descending
$i = 1;
?>

<table> 
                <tr> 
                <th>Player Name:</th>
                <th>Score:</th>
        </tr>
<?php

while ($row = $q->fetch()){ 
        echo "<tr><td>".$i++."</td><td>".$row["username"]."</td><td>".$row["score"]."</td></tr>\n";
}

?>
</table>
