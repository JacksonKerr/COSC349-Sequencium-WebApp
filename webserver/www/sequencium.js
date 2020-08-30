$(document).ready(function() {
    opponentColour = "#FF6347" // Tomato
    playerColour = "#3CB371" // Medium Sea Green
    selectedTileColour = "#f5ef42" // Lemon Yellow

    selectedSpaceButtonText = "Selected Space"
    emptySpaceButtonText = "Empty Space"

    opponentStrategy = null
    opponentMirror = null

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
        cliffData += numRows+" "+numCols+" "+opponentMirror+" "+opponentStrategy+" "

        for (row of Object.values(board)) {
            for (val of Object.values(row)) {
                cliffData += val+" "
            }
        }

        $.ajax({
            url:"getMove.php",
            data: {board:cliffData},
            type: "GET",

            success:function(cliffMove)
            {
                cliffMove = JSON.parse(cliffMove)
                cliffRow = cliffMove[0]+1
                cliffCol = cliffMove[1]+1
                cliffVal = cliffMove[2]
                editTile(cliffRow, cliffCol, cliffVal, false)
            }
        })
    }

    function createGame() {
        numRows = parseInt($("#numRows").val())
        numCols = parseInt($("#numCols").val())

        opponentStrategy = $("#cliffStrategy").val()
        opponentMirror = $("#mirrorMoves").prop("checked")

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

        largestNeighVal = parseInt(getLargestNeighbourValue(rowNum, colNum))

        // Check if space on board is available TODO and that the space is a valid move
        if (board[rowNum][colNum] == 0 && largestNeighVal > 0) {
            if (oldSelection != null) {
                // Remove the colour and text from the old selected space if one exists
                oldButton = $("#row"+oldSelection[0]+" .col"+oldSelection[1])
                oldButton.css("background-color", "")
                oldButton.text(emptySpaceButtonText);
            }

            // Set the colour of the new selected space, and change the text to 'selected'
            $(this).css("background-color", selectedTileColour)
            $(this).html(selectedSpaceButtonText)
            $("#moveValInput").val(largestNeighVal+1)
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
});