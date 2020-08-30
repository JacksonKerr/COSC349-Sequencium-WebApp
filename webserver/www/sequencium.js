$(document).ready(function() {
    opponentColour = "#FF6347" // Tomato
    playerColour = "#3CB371" // Medium Sea Green
    selectedTileColour = "#f5ef42" // Lemon Yellow

    selectedSpaceButtonText = "Selected Space"
    emptySpaceButtonText = "Empty Space"

    board = {}
    selectedSpace = null

    function main() {
        console.log("Sequencium.js Loaded")
        createGame()

        $("#createGameButton").click(createGame)
        $("#makeMoveButton").click(makeMove)
    }

    function makeMove() {
        // Get value of move and update Board
        moveVal = $("#moveValInput").val()
        editTile(selectedSpace[0], selectedSpace[1], moveVal, true)
        
        // The space is filled, and therefore no longer selected
        selectedSpace = null

        makeOpponentMove()
    }

    function makeOpponentMove() {
        // Ajax call to get opponents next move from the server
        cliffData = ""

        // First line holds number of numRows"space"numCols
        numRows = Object.values(board).length
        numCols = Object.values(board[1]).length
        cliffData += numRows+" "+numCols+" "

        for (row of Object.values(board)) {
            for (val of Object.values(row)) {
                cliffData += val+" "
            }
        }
        console.log("Final cliffData:\n"+cliffData)

        $.ajax({
            url:"getMove.php",
            data: {board:cliffData},
            type: "GET",

            success:function(blah)
            {
                console.log(blah)
            }
        })
    }

    function createGame() {
        numRows = parseInt($("#numRows").val())
        numCols = parseInt($("#numCols").val())

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

        editTile(1, 1, 1, true)
        editTile(numRows, numCols, 1, false)

        $("#seqBoard button").click(selectMove)
    }

    function selectMove() {
        // If another move was previously selected, set it back to grey
        oldSelection = selectedSpace

        // Get coords of selected move
        rowId = $(this).parent().attr("id")
        colClass = $(this).attr("class")
        rowNum = parseInt(rowId[rowId.length-1])
        colNum = parseInt(colClass[colClass.length-1])

        selectedSpace = [rowNum, colNum]

        // Check if space on board is available
        if (board[rowNum][colNum] == 0) {
            if (oldSelection != null) {
                // Remove the colour and text from the old selected space if one exists
                console.log("changing button")
                oldButton = $("#row"+oldSelection[0]+" .col"+oldSelection[1])
                oldButton.css("background-color", "")
                oldButton.text(emptySpaceButtonText);
            }

            // Set the colour of the new selected space, and change the text to 'selected'
            $(this).css("background-color", selectedTileColour)
            $(this).html("Move Value:<input id='moveValInput' type='number' id='numCols' value='3' name='numCols'>")
        } else {
            selectedSpace = oldSelection
        }
    }

    // --------------------------------------------------------------------- //
    //                           Helper Functions                            //
    // --------------------------------------------------------------------- //
    function editTile(row, col, value, isPlayer) {
        // Update displayed board
        tile = $(("#row"+row)).find((".col"+col))
        tile.text(value)
        colour = isPlayer ? playerColour : opponentColour
        tile.css("background-color", colour);

        value = isPlayer ? value : -1*value
        board[row][col] = value
    }

    main()
});