<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<html>
<head>
  <title>Test page</title>
  <script src="jquery.min.js"></script>
  <script src="sequencium.js"></script>
  <link rel="stylesheet" type="text/css" href="style.css"> 
</head>

<body>
<h1>Sequencium: Man vs Machine</h1>
  <div id="seqGame">



    <div id="gameOptions">

      <div id="sizeOptions">
        <label for="numRows">Rows:</label>
        <input type="number" id="numRows" value="3" name="numRows">
        <label for="numCols">Cols:</label>
        <input type="number" id="numCols" value="3" name="numCols">
      </div>
      
      <div id="difficultyOptions">
        <label for="mirrorMoves">Use Mirror Strategy</label>
        <input type="checkbox" id="mirrorMoves" value="1">
        
        <label for="cliffStrategy">Opposition Strategy:</label>
        <select id="cliffStrategy">
          <option value="minimax">Minimax Lookahead</option>
          <option value="random">Random</option>
        </select> 
      </div>
          
      <button id="createGameButton">Create Game</button>
    </div>





    <div id="seqBoard">
    </div>

    <div id="gameButtons">
      <label for="makeMoveButton">Move Size:</label>
      <input id='moveValInput' type='number' id='numCols' value='0' name='numCols'>
      <button id="makeMoveButton">Make Move</button>
    </div>

  </div>



</table>
</body>
</html>