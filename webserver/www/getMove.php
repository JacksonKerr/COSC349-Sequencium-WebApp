<?php 
// Get the board given in the ajax call
$board = $_GET['board'];

// Get move from seqAgent server. 
// This is very insecure, and in a real world application 
// the input would need to be sanatised.
echo shell_exec("echo $board | nc 192.168.5.3 8081");
?>