$(document).ready(function() {
    opponentColour = "#FF6347" // Tomato
    playerColour = "#3CB371" // Medium Sea Green

    board = {}
    selectedSpace = []

    function main() {
        console.log("Sequencium.js Loaded")
        $("#createGameButton").click(createGame)
    }


    function createGame() {
        // Initialise board of correct size
        numRows = parseInt($("#numRows").val())
        numCols = parseInt($("#numCols").val())

        initialiseBoard(numRows, numCols)
    }

    function selectMove() {
        rowId = $(this).parent().attr("id")
        colClass = $(this).attr("class")

        rowNum = parseInt(rowId[rowId.length-1])
        colNum = parseInt(colClass[colClass.length-1])

        // Check if space on board is available, might have to contact server to check

        selectedSpace = [rowNum, colNum]
    }


    // --------------------------------------------------------------------- //
    //                             UI Functions                              //
    // --------------------------------------------------------------------- //
    function editTile(row, col, value, isPlayer) {
        tile = $(("#row"+row)).find((".col"+col))
        tile.text(value)
        colour = isPlayer ? playerColour : opponentColour
        tile.css("background-color", colour);
    }

    function initialiseBoard(numRows, numCols) {

        // Creates grid of buttons to represent board
        $("#seqBoard").empty()
        for (row = 1; row < numRows+1; row++) {
            $("#seqBoard").append($("<div id=row"+row+"></div>"))
            for (col = 1; col < numCols+1; col++) {
                $(("#row"+row)).append($("<button class=col"+col+">Empty Space</button>"))
            }
        }

        // Set up board object with all empty spaces
        for (row = 1; row < numRows+1; row++) {
            board[row] = {}
            for (col = 1; col < numCols+1; col++) {
                board[row][col] = 0
            }
        }
        // TODO create object to track board and pass to server


        humanMove()
    }

    function humanMove() {
        $("#seqBoard button").click(selectMove)
    }

    main()
});