package com.example.cw2kr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;


/**
 * @author 215732
 * UI class combines game functionality with an UI representation
 * simple borderpane structure provides place to store gridpane and respective grid of buttons
 * which represent the field.
 *
 * Entails click event as well - which provides methods to access the lists in the checkersBoard class to identify
 * possible moves of a given tile. Concluded with a potential game loop that ends the game with alert boxes once
 * a respective player has lost all checkers
 *
 */
public class gamePlay extends Application {


    public Button[][] field;
    public checkersBoard board;
    private GridPane grid = new GridPane();
    //public int xdest,ydest;
    public int xorigin, yorigin;
    public Checkers selected;
    public int count;
    public boolean firstclick = true;
    public int storex,storey;
    public BorderPane pane;


    /**
     * start method
     * starts and arranges stage - calls different elements of borderpane
     */

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(gamePlay.class.getResource("hello-view.fxml"));
        //setting up borderpane - see report for layout and how borderpane is structured
        BorderPane pane = new BorderPane();
        board = new checkersBoard(8, 8);
        //Scene scene = new Scene(fxmlLoader.load());
        Scene scene = new Scene(pane, 680, 718);
        pane.setTop(createToolBar());
        pane.setCenter(startGrid());
        this.pane = pane;
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.show();

    }


    /**
     * createToolBar method - creates toolBar with various buttons
     * with varying functionality e.g. difficulty of ai player
     * The toolbar provides some further functionality meeting the requirements of the game. A help/instructions menu
     * is added to help users. The start and restart buttons offer players important functionality in controlling the game.
     *
     *
     * @return toolbar
     */
    public ToolBar createToolBar() {

        ToolBar toolbar = new ToolBar();
        Popup popup = new Popup();

        Button startButton = new Button("Start Game");
        toolbar.getItems().add(startButton);

        startButton.setOnAction(event -> {
            startGame();
        });

        //Separator between different toolbar elements
        toolbar.getItems().add(new Separator());


        Button restartButton = new Button("Restart Game");
        toolbar.getItems().add(restartButton);

        restartButton.setOnAction(event -> {
            startGame();
        });

        toolbar.getItems().add(new Separator());

        Button helpButton = new Button("Help");
        toolbar.getItems().add(helpButton);

        helpButton.setOnAction(event -> {
        //Click event for Game Information - accessible for users who require information on how to play
            Stage helpStage = new Stage();
            VBox box = new VBox();
            Label label = new Label("Game Information ");
            //Text Area
            TextArea checkersInfo = new TextArea("General Rules: " +
                    "Checkers is a board game played on a board set up in 8 columns and 8 rows. Similar to chess it" + "\n" +
                    "includes tiles that can be captured by enemy player.Each player has 12 checkers and can either " + "\n" +
                    "move or jump with these checkers.The Game is over when either of the players have captured all the enemy " + "\n" +
                    "tiles or when either of the player is unable to make a move anymore." + "\n" + "\n" +

                    "Type of moves: " + "In checkers that are capturing and non-capturing moves. That entail certain" + "\n" +
                    "conditions. All moves can only be completed diagonally and one can only utilise a jump" + "\n" +
                    "when one has the ability to capture a player. If this is the case however each player is however also" + "\n" +
                    "forced to make a jump. This forced capture can continue when one jump enables a player to complete another " + "\n" +
                    "capture as well. " + "\n" + "\n" +

                    "Special events: " +
                    "If someone reaches the their respective other side of the board with a tile" + "\n" +
                    "that respective tile will be crowned king, which offers some new movements. A king is able" + "\n" +
                    " to move forwards as well as backwards. This however does not mean the king is safe from being" + "\n" +
                    "captured, as if a king if captured by an opposing tile, that tile is automatically crowned as king");

            checkersInfo.setEditable(false);
            box.getChildren().addAll(label, checkersInfo);
            Scene stageScene = new Scene(box, 700, 250);
            helpStage.setScene(stageScene);
            helpStage.show();

        });


        toolbar.getItems().add(new Separator());

        ComboBox difficultyCombobox = new ComboBox();
        difficultyCombobox.getItems().addAll("Easy", "Medium", "Hard", "Challenge");
        difficultyCombobox.setValue("Difficulty");
        toolbar.getItems().add(difficultyCombobox);


        startButton.setOnAction(event -> {
            startGrid();
        });

        //returns entire toolbar - with different elements separated
        return toolbar;


    }


    /**
     * starGrid method - creates grid of buttons, which represent the respective checker tiles and the
     * respective fields as well. Includes style preferences as well and event handling
     * The start grid method also calls methods that check the validity of moves and the potential movies that can be made
     *
     * By calling checkersboard objects we can manipulate the way tiles function and create a grid of buttons
     *
     * @return grid
     */
    public GridPane startGrid() {
        GridPane grid = new GridPane();
        //startGame();


        //new field of buttons - represent tiles and respective fields themselves - 2D array
        field = new Button[8][8];

        //Loop through buttons in grid - add circles and buttons
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                //looping  through 2D array of buttons to set up grid and set up checkers
                //empty button
                Button btn = makeCell(row, col);
                //adds everything to the grid
                grid.add(field[col][row], col, row);

                if(board.fieldtile(row, col) != null) {
                    Circle tile = new Circle(15, (board.fieldtile(row, col).getColour() == Colour.beige) ? Color.BEIGE : Color.BLACK);
                    //calling checkersBoard object
                    if (board.checkersBoard[row][col].getisKing()) {
                        tile.setStroke(Color.GOLD);
                    }
                    //set btn as graphic
                    btn.setGraphic(tile);
                    field[col][row] = btn;
                }
            }
        }
        this.grid = grid;
        return grid;
    }

    /**
     * makeCell method - creates new button and is called when creating a new button
     * method is of type  button and refers back to startGrid
     * @param row
     * @param col
     * @return
     */
    private Button makeCell(int row, int col) {
        Button btn = new Button();
        btn.setMinSize(85, 85);
        btn.setMaxSize(85, 85);
        int x = row;
        int y = col;
        //button event - is functional but change to different location only takes place in command line
        //the checker in the UI does not complete the move but when the algorithm is run it completes a game function
        btn.setOnMouseClicked(event2 -> {
            System.out.println("x =" + x + "y =" + y);
            //if specific checker has been moved to new location
            boolean moved = selectChecker(x,y);
            if (moved) {
                //update board and change the colour depending on the colour that was clicked
//                updateBoard(x, y, (board.fieldtile(x, y).getColour() == Colour.beige) ? Color.BEIGE : Color.BLACK);
                //update board by resetting the pane in the center of the border pane
                pane.setCenter(null);
                pane.setCenter(startGrid());
            }
            else {
                xorigin = x;
                yorigin = y;
            }
        });
        field[col][row] = btn;
        System.out.print(row + col);
        //sets checkered board - colour specified in if statement
        String color;
        //using modulo operator to reach checkered pattern
        if ((row + col) % 2 == 0) {
            color = "#ffcd78";
        } else {
            color = "#824c25";
            // color = "#ffffff";
        }
        btn.setStyle("-fx-background-color: " + color + ";");
        return btn;
    }




    /**
     * gameEnding method - creates alerts depending on the result of the game
     * Runs throughout game until specific conditions are met within the game
     *
     */
    public void gameEnding() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //if no more blackcheckers
        if (board.blackcheckers == 0) {

            //sets alert
            alert.setTitle("All black tiles have been captured");
            alert.setHeaderText("Human player has won the game");
            alert.setContentText("Quit or play again");
        }
        //if no more beigecheckers
        if (board.beigecheckers == 0) {

            //sets alert
            alert.setTitle("All beige tiles have been captured");
            alert.setHeaderText("AI player has won the game!");
            alert.setContentText("Quit or play again");
        }

        ButtonType buttonTypeOne = new ButtonType("Restart");
        ButtonType buttonTypeTwo = new ButtonType("Quit");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        //Buttons to leave the game
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            startGrid();
        } else if (result.get() == buttonTypeTwo) {
            Platform.exit();
        }

    }


    /**
     * starGame method - contains main game loop
     * is referenced in main method
     *
     */
    public void startGame(){
        //checks gameisOver boolean from checkersBoard class
        while (board.gameisOver()){
            //select checker if it is colours turn
//                selectChecker(board.row, board.col);

            //call game ending - refers to class that checks if there are any checkers of a given colour left
//            gameEnding();

        }
    }


    /**
     * * selectChecker - referenced in click event
     *   looks at all available moves and is referenced as away to select a checker
     *
     *
     * @param row
     * @param col
     * @return
     */
    public boolean selectChecker(int row, int col) {
        boolean moved = false;
        if(selected != null) {
            //a circle not selected - find all availableMoves - references possibleMoves method - same list structure
            for (HashMap<ArrayList<Integer>, ArrayList<Checkers>> moves : board.availableMoves) {
                ArrayList<Integer> pos = new ArrayList<>();
                pos.add(row);
                pos.add(col);

                //if move contains key i.e. location of clicked area then it can be moved
                if (moves.containsKey(pos)) {
                    board.move(row, col, selected);
                    moved = true;
                    //select to null wants move is ended - makes sure that
                    selected = null;
                }
            }

            if (!moved) {
                selected = null;
                selectChecker(row, col);
            }

        }else if(board.fieldtile(row,col) != null){
            // this runs by default so we know it works
            if (board.turn == board.fieldtile(row,col).getColour()) {
                //check available moves of selected tile
                selected = board.fieldtile(row, col);
                //move is added to selected move in possible moves.
                // this doesn't work
                board.availableMoves = board.possibleMoves(selected);

            }
        }
        //returns if checker was moved or not
        return moved;
    }

    /**
     *toggleCircle method - takes button and colour as parameter and sets up a new colour depending on the possible
     * moves available for a respective tile depending if it's a normal tile or a king
     *
     * @param button
     * @param color
     * @return button
     */
    public Button toggleCircle(Button button, Color color){
        //new circle
        Circle circle = new Circle(10,color);
        //setting the graphic of new field that is selected
        if(button.getGraphic() == null){
            button.setGraphic(circle);
        }
        else{
            //setGraphic to null
            button.setGraphic(null);
        }
        return button;

    }

    public static void main(String[] args) {

        launch();
    }
}






//        for (int row = 0; row < field.length; row++) {
//            for (int col = 0; col < field[row].length; col++) {
//                if (board.checkersBoard[col][row].getisBeige()) {
//                    button = new Button();
//                    button.setMinSize(85, 85);
//                    button.setMaxSize(85, 85);
//
//                }
//                 else if (board.checkersBoard[col][row].getisBlack()) {
//                    button = new Button();
//                    button.setMinSize(85, 85);
//                    button.setMaxSize(85, 85);
//            }
//
//                 else {
//                    button = new Button();
//                    button.setMinSize(85, 85);
//                    button.setMaxSize(85, 85);
//                    field[row][col] = button;
//                    System.out.print(row + col);
//                }
//                String color;
//                if ((row + col) % 2 == 0) {
//                    color = "#ffcd78";
//                } else {
//                    color = "#824c25";
//                    // color = "#ffffff";
//                }
//                button.setStyle("-fx-background-color: " + color + ";");
//                grid.add(field[row][col], col, row);
//
//            }
//        }
//
//
//        for (int row = 0; row < field.length; row++) {
//            for (int col = 0; col < field[row].length; col++) {
//                if (board.checkersBoard[col][row].getisBeige()) {
//
//                    Circle beigetile = new Circle(15, Color.BEIGE);
//                    if (board.checkersBoard[col][row].getisKing()) {
//                        beigetile.setStroke(Color.GOLD);
//                    }
//                    button.setGraphic(beigetile);
//                    field[row][col] = button;
//
//
//                    beigetile.setOnMousePressed(event -> {
//                        anchorX = event.getSceneX();
//                        anchorY = event.getSceneY();
//                    });
//                    beigetile.setOnMouseDragged(event -> {
//                        beigetile.setTranslateX(event.getSceneX() - anchorX);
//                        beigetile.setTranslateY(event.getSceneY() - anchorY);
////                        Image image = new Image("white.png");  //pass in the image path
////                        scene.setCursor(new ImageCursor(image));
//
//                    });
//                    beigetile.setOnMouseReleased(event -> {
//                        //commit changes to LayoutX and LayoutY
//                        beigetile.setLayoutX(event.getSceneX() - mouseOffsetFromNodeZeroX);
//                        beigetile.setLayoutY(event.getSceneY() - mouseOffsetFromNodeZeroY);
//                        //clear changes from TranslateX and TranslateY
//                        beigetile.setTranslateX(0);
//                        beigetile.setTranslateY(0);
//                    });
//
//
//            }
//                else if (board.checkersBoard[col][row].getisBlack()) {
//                    Circle blacktile = new Circle(15, Color.BLACK);
//                    if (board.checkersBoard[col][row].getisKing()) {
//                        blacktile.setStroke(Color.GOLD);
//                    }
//                    button.setGraphic(blacktile);
//                    field[row][col] = button;
//
//
//                    blacktile.setOnMousePressed(event2 -> {
//                        anchorX = event2.getSceneX();
//                        anchorY = event2.getSceneY();
//                    });
//                    blacktile.setOnMouseDragged(event2 -> {
//                        blacktile.setTranslateX(event2.getSceneX() - anchorX);
//                        blacktile.setTranslateY(event2.getSceneY() - anchorY);
////                        Image image2 = new Image("black.png");  //pass in the image path
////                        scene.setCursor(new ImageCursor(image2));
//                    });
//                    blacktile.setOnMouseReleased(event2 -> {
//                        //commit changes to LayoutX and LayoutY
//                        blacktile.setLayoutX(event2.getSceneX() - mouseOffsetFromNodeZeroX);
//                        blacktile.setLayoutY(event2.getSceneY() - mouseOffsetFromNodeZeroY);
//                        //clear changes from TranslateX and TranslateY
//                        blacktile.setTranslateX(0);
//                        blacktile.setTranslateY(0);
//                    });
//
//                }
//                grid2.add(field[row][col], col, row);
//
//                }
//
//            }
//        pane2.getChildren().addAll(grid,grid2);
//        return pane2;
//
//    }

//
//if (field[x][y] == test) {
//        System.out.println("hhhhilhhjkbhjhvhhhc");
//        count += 1;
//        xdest = x;
//        ydest = y;
//        if (count == 1) {
//        xorigin = x;
//        yorigin = y;
//        } else if (count == 2) {
//        //board.possibleMoves(board.fieldtile(x,y));
//        System.out.println("IWAANNADIEEEEEEEE");
////                                ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>>  possMoves = board.possibleMoves(board.fieldtile(x,y));
////                                int i;
////                                for(i = 0; i < board.availableMoves.size(); i++){
////                                    System.out.println("ffffffff");
////                                    if(board.availableMoves.size() == 2){
////                                        System.out.println("whaaaat");
////                                        toggleCircle(button,Color.PURPLE);
////                                    }
////                                    else{
////                                        toggleCircle(button,Color.PURPLE);
////                                    }
////                                }
//
//        selectChecker(x,y);
//
//
//        field[xorigin][yorigin] = toggleCircle(field[x][y], null);
//        count = 0;
//
//
//        }
//
//        }
//                if (board.checkersBoard[col][row].getColour() == Colour.beige) {
//                    System.out.println("Hello");
//                    btn = new Button();
//                    btn.setMinSize(85, 85);
//                    btn.setMaxSize(85, 85);
//                    //Circle
//                    Circle beigetile = new Circle(15, Color.BEIGE);
//                    if (board.checkersBoard[col][row].getisKing()){
//                        beigetile.setStroke(Color.GOLD);
//                    }
//                    //Set cricle as graphic
//                    btn.setGraphic(beigetile);
//                  //  field[row][col] = button;
//                    field[row][col] = btn;
//                    int x = row;
//                    int y = col;
//
//                    Button btn = field[row][col];
//                    //beigeTile click event to do actual movements
//                    btn.setOnMouseClicked(event -> {
////                        if(firstclick){
////                            storex = x;
////                            storey = y;
////                            firstclick = false;
////                        }
////                        else {
////                            if (board.possibleMoves(board.fieldtile(storex, storey)).contains(fieldtile)) {
////                                firstclick = true;
////                            }
////                        }
//
//                        selectChecker(x, y);
//
//                    });
//
//
//                }
//                else if (board.checkersBoard[col][row].getColour() == Colour.black) {
//                    btn = new Button();
//                    btn.setMinSize(85, 85);
//                    btn.setMaxSize(85, 85);
//                    //Black Circle
//                    Circle blacktile = new Circle(15, Color.BLACK);
//                    //calling checkersBoard object
//                    if (board.checkersBoard[col][row].getisKing()) {
//                        blacktile.setStroke(Color.GOLD);
//                    }
//                    btn.setGraphic(blacktile);
//                    field[row][col] = btn;
//
//                    Button test2 = field[row][col];
//                    int x = row;
//                    int y = col;
//
//
//                    //blackTile click event to do actual movements
//                    test2.setOnMouseClicked(event2 -> {
////                        if (field[x][y] == test2) {
////                            System.out.println("hhhhilhhjkbhjhvhhhc");
////                            count += 1;
////                            xdest = x;
////                            ydest = y;
////                            if (count == 1) {
////                                xorigin = x;
////                                yorigin = y;
////                            } else if (count == 2) {
//////
//////                                System.out.println("ejekkjhfkwjhwejhwekjhfekjwehf");
//////                                for(int i = 0; i < board.getavailableMoves().size(); i++){
//////                                    //System.out.println(possMoves);
//////                                    if(board.getavailableMoves().size() == 2){
//////                                        toggleCircle(button,Color.PURPLE);
//////                                    }
//////                                    else{
//////                                        toggleCircle(button,Color.PURPLE);
//////                                    }
//////                                }
////                                System.out.println("x =" + x + "y =" + y);
////
////
////                                //int possMove1X = possMoves.g
////
//////                                field[xdest][ydest] = toggleCircle(field[x][y], Color.BLACK);
//////                                field[xorigin][yorigin] = toggleCircle(field[x][y], null);
//////                                count = 0;
////
////                                }
////                            }
//                        System.out.println("x =" + x + "y =" + y);
//                        selectChecker(x,y);
//                    });
//
//
//                } else {
//
//            }
