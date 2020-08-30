<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<html>
<head>
  <title>Sequencium Challenge</title>
  <script src="jquery.min.js"></script>
  <script src="sequencium.js"></script>
  <link rel="stylesheet" type="text/css" href="style.css"> 
</head>

<body>
<h1>Sequencium Challenge:</h1>
  <div id="seqGame">
    <div id="gameOptions">
      <h2>Settings:</h2>
      <div id="r1">
        <div id="sizeOptions">
          <label for="numRows">Rows:</label>
          <input type="number" id="numRows" value="6" name="numRows">
          <label for="numCols">Cols:</label>
          <input type="number" id="numCols" value="6" name="numCols">
        </div>
        
        <div id="mirrorOptions">
          <label for="mirrorMoves">Use Mirror Strategy</label>
          <input type="checkbox" id="mirrorMoves" value="1">
        </div>
      </div>
      
      </br>
      
      <div id="r2">
        <div id="startPlayerOptions">
          <label for="startingPlayer">Starting Player:</label>
          <select id="startingPlayer">
            <option value="human">Human</option>
            <option value="computer">Computer</option>
          </select> 
        </div>

        <div id=stratOptions>
          <label for="cliffStrategy">CPU Strategy:</label>
          <select id="cliffStrategy">
            <option value="minimax">Minimax Lookahead</option>
            <option value="random">Random</option>
          </select> 
        </div>

      </div>
      <div id="r3">
      <button id="createGameButton">Create Game</button>
      </div>
    </div>

    <h3 id="moveIndicator"></h3>
    <div id="seqBoard">
    </div>

    <div id="gameButtons">
      <label for="makeMoveButton">Move Size:</label>
      <input id='moveValInput' type='number' id='numCols' value='0' name='numCols' readonly>
      <button id="makeMoveButton">Make Move</button>
      <button id="passMoveButton">Pass Turn</button>
    </div>
  </div>

  <div id="highScores"></div>



</table>
</body>
</html>