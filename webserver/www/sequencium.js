/**
 * sequencium.js
 * 
 * - Jackson Kerr
 */
$(document).ready(function() {
    opponentColour = "#FF6347" // Tomato
    playerColour = "#3CB371" // Medium Sea Green
    drawColour = "#DE8921" // Orange

    emptySpaceButtonText = "Empty"

    playerTurnText = "Human's Turn"
    opponentTurnText = "Computer is Thinking..."

    opponentStrategy = null
    opponentMirror = null

    gameScore = null

    board = {}

    /**
     * Run on page load
     */
    function main() {
        $("#createGameButton").click(createGame)
        getScoreList();
    }

    /**
     * Gets a move from the server and uses it to update the game board
     */
    function makeOpponentMove() {
        // Disable buttons while the move is retrieved and applied
        $("button").prop("disabled", true)
        $("#moveIndicator").text(opponentTurnText)
        $("#moveIndicator").css("color", opponentColour)
        
        // Ajax call to get opponents next move from the server
        cliffData = ""
        
        numRows = Object.values(board).length
        numCols = Object.values(board[1]).length

        // First line holds info about the current game
        cliffData += numRows+" "+numCols+" "+opponentMirror+" "+opponentStrategy+" "
        
        // Create a string representation of the board
        for (row of Object.values(board)) {
            for (val of Object.values(row)) {
                cliffData += val+" "
            }
        }
        
        // Get's a move from the server
        $.ajax({
            url: "getMove.php",
            data: {board:cliffData},
            type: "GET",
            
            success:function(cliffMove) {
                cliffMove = JSON.parse(cliffMove)
                // Note: Server uses 0-indexed board
                cliffRow = cliffMove[0]+1
                cliffCol = cliffMove[1]+1
                cliffVal = cliffMove[2]
                
                // Server returns a negative movement value 
                // in the case where it has no move
                if (cliffVal >= 1) {
                    editTile(cliffRow, cliffCol, cliffVal, false)
                }

                if (checkGameComplete()) {
                    return
                } else {
                    // Enable game interaction controls
                    $("#moveIndicator").text(playerTurnText)
                    $("#moveIndicator").css("color", playerColour)
                    
                    $("button").prop("disabled", false)
                }
            },
        })
    }

    /**
     * Gets the game settings from the input fields and creates a board of given size
     */
    function createGame() {
        $("#passMoveButton").click(makeOpponentMove)

        numRows = parseInt($("#numRows").val())
        numCols = parseInt($("#numCols").val())

        opponentStrategy = $("#cliffStrategy").val()
        opponentMirror = $("#mirrorMoves").prop("checked")

        startingPlayer = $("#startingPlayer").val()

        $("#moveIndicator").text("Human's Turn")
        $("#moveIndicator").css("color", playerColour)

        
        // Set up board object with all empty spaces
        board = {}
        for (row = 1; row < numRows+1; row++) {
            board[row] = {}
            for (col = 1; col < numCols+1; col++) {
                board[row][col] = 0
            }
        }

        // Show board of the given dimensions
        $("#seqBoard").empty()
        for (row = 1; row < numRows+1; row++) {
            $("#seqBoard").append($("<div id=row"+row+"></div>"))
            for (col = 1; col < numCols+1; col++) {
                $(("#row"+row)).append($("<button class=col"+col+">"+emptySpaceButtonText+"</button>"))
            }
        }

        // Place starting moves
        editTile(1, 1, 1, true)
        editTile(numRows, numCols, 1, false)

        if (startingPlayer === "computer") makeOpponentMove()

        $("#seqBoard button").click(selectMove)
    }

    /**
     * Called when the user clicks on a space on the board. 
     * Checks the move is valid then makes the move and asks 
     * the server for the opponents next move.
     */
    function selectMove() {
        // Get coords of selected move
        rowId = $(this).parent().attr("id")
        colClass = $(this).attr("class")
        rowNum = parseInt(rowId[rowId.length-1])
        colNum = parseInt(colClass[colClass.length-1])

        largestNeighVal = parseInt(getLargestNeighbourValue(rowNum, colNum))

        // Check if space on board is available and that the space is a valid move
        if (board[rowNum][colNum] != 0 || largestNeighVal < 1) return


        // Get value of move and update Board
        moveVal = largestNeighVal+1
        editTile(rowNum, colNum, moveVal, true)

        makeOpponentMove()
    }

    /**
     * Checks if all spaces on the board is taken and if so displays a field 
     * for the user to enter their name so that their score can be saved 
     * on the server.
     * 
     * @return true if the board is full, else false
     */
    function checkGameComplete() {
        // Exit if game is complete (All spaces are taken)
        boardArr = Object.values(board)
        opponentScore = 0
        playerScore = 0
        for (row of Object.values(boardArr)) {
            for (val of Object.values(row)) {
                // If game is incomplete

                if (val == 0) {
                    console.log("incomplete")
                    return false
                }
                if (val < opponentScore) opponentScore = val
                if (val > playerScore) playerScore = val
            }
        }

        // GAME IS COMPLETE IF WE GET HERE:
        console.log("game completed")

        gameScore = playerScore + opponentScore

        // If it's a draw
        if (gameScore == 0) {
            $("#moveIndicator").text("Draw! Score: "+gameScore)
            $("#moveIndicator").css("color", drawColour)
        }
        // If human wins
        if (gameScore > 0) {
            $("#moveIndicator").text("You Win! Score: "+gameScore)
            $("#moveIndicator").css("color", playerColour)
        }   
        // If computer player wins
        if (gameScore < 0) {
            $("#moveIndicator").text("CPU Wins! Human Score: "+gameScore)
            $("#moveIndicator").css("color", opponentColour)
        }

        // Show name entry field
        $("#nameInput").empty()
        $("#nameInput").append(  "<label for='playerName'>Your Name Here: </label>"
                                +"<input id='playerName' type='text'></br>"
                                +"<button id='submitScoreButton'>Submit Score</button>")
        $("#submitScoreButton").click(submitScore)

        return true
    }

    /**
     * Submits the score and name from the post-game input field
     */
    function submitScore() {
        // Get name
        playerName = $("#playerName").val()

        // Ajax submit score
        $.ajax({
            url: "updateScore.php",
            data: {score:gameScore, name:playerName},
            type: "GET",
            success:function() {getScoreList()}
        })
        // Clear submit score
        $("#nameInput").empty()
        $("button").prop("disabled", false)
    }

    /**
     * Gets a list of highscores from the database server and displays them
     */
    function getScoreList() {
        $("#highScores").empty()
        $.ajax({
            url: "getScore.php",
            type: "GET",
            success:function(response) {
                $("#highScores").append(response)
            },
        })
    }


    // --------------------------------------------------------------------- //
    //                           Helper Functions                            //
    // --------------------------------------------------------------------- //

    /**
     * Inserts a move onto a given space on the board.
     * 
     * @param {int} row The row of the move's location.
     * @param {int} col The column of the move's location.
     * @param {int} value The value of the move.
     * @param {boolean} isPlayer If true, places a human move, else a CPU move.
     */
    function editTile(row, col, value, isPlayer) {
        // Update displayed board
        tile = $(("#row"+row)).find((".col"+col))
        tile.text(value)
        colour = isPlayer ? playerColour : opponentColour
        tile.css("background-color", colour);

        value = isPlayer ? value : -1*value
        board[row][col] = value
    }

    /**
     * Returns the value of the largest neighbouring square.
     * 
     * @param {int} row 
     * @param {int} col
     */
    function getLargestNeighbourValue(row, col) {
        neighbours = getNeighbours(row, col)
        largestNeighVal = -999999999999999

        for (neigh of neighbours) {
            neighRow = neigh[0]
            neighCol = neigh[1]

            neighVal = board[neighRow][neighCol]

            if (neighVal > largestNeighVal) {
                largestNeighVal = neighVal
            }
        }
        return largestNeighVal
    }

    /**
     * Returns a list of 2d lists representing the coordinates 
     * of a spaces neighbours.
     * 
     * @param {int} row 
     * @param {int} col 
     */
    function getNeighbours(row, col) {
            numRows = Object.values(board).length
            numCols = Object.values(board[1]).length

            possRows = []
            possCols = []
            returnList = []
            
            // If Row or value is invalid
            if (row < 1 || row > numRows || col < 1 || col > numCols) {
                return returnList;
            }

            possRows.push(row)
            possCols.push(col)
    
            if (row > 1) possRows.push(row-1)
            if (col > 1) possCols.push(col-1)
    
            if (row < numRows) possRows.push(row+1)
            if (col < numCols) { possCols.push(col+1);}
    
            for (cRow of possRows) {
                for (cCol of possCols) {
                    if (cRow == row && cCol == col) continue
                    returnList.push([cRow, cCol])
                }
            }
            return returnList;
    }


    main()
});