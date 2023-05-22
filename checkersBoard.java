package com.example.cw2kr;

import java.util.*;
import java.lang.Math;

/**
 * @author 215732
 * checkersBoard class
 * holds main game functionality - setting up checkersBoard and checkers at respective position - creates
 * board of Checkers object
 * further methods on movements, validation of moves and special conditions such as forced capture, multilegcapture,
 * regicide and king conversion
 */
public class checkersBoard {


    int finishRow, finishCol;
    public int blackcheckers = 12;
    public int beigecheckers = 12;
    public Checkers[][] checkersBoard;
    public boolean gameover = false;
    public int[] jumplist;
    public ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>> availableMoves;
    public int count = 0;
    public Colour turn;


    checkersBoard(int row, int col) {

        this.finishRow = finishRow;
        this.finishCol = finishCol;
        this.jumplist = jumplist;
        this.turn = Colour.black;

        //constructer - creates a new checkersBoard of checkers
        //poulates it with the given checker tiles and their correct location - see report for correct layout
        this.checkersBoard = new Checkers[row][col];
        for (int temprow = 0; temprow < 8; temprow++) {
            for (int tempcol = 0; tempcol < 8; tempcol++) {

                 checkersBoard[temprow][tempcol] = null;
                //use of modulo operator - similar in modulo class to specify where exactly checkers should be positioned
                //go through even rows and change accordingly
                if (tempcol % 2 == (temprow + 1) % 2) {
                    if (temprow < 3) {
                        checkersBoard[temprow][tempcol] = new Checkers();
                        checkersBoard[temprow][tempcol].setColour(Colour.black); //calling enum
                        checkersBoard[temprow][tempcol].setPos(temprow, tempcol);
                    } else if (temprow > 4) {
                        checkersBoard[temprow][tempcol] = new Checkers();
                        checkersBoard[temprow][tempcol].setColour(Colour.beige);
                        checkersBoard[temprow][tempcol].setPos(temprow, tempcol);
                    }
                }

            }
        }
    }


    /**
     * field method of type checkers
     * eturns single field in the specified location of the board
     *
     * @return checkersBoard[row][col]
     */
    public Checkers fieldtile(int row, int col) {
        return checkersBoard[row][col]; //returns 1 field on the checkersBoard
    }

    /**
     * getter method for entire checkersBoard
     *
     * @return checkersBoard
     */
    public Checkers[][] getcheckersBoard() {
        return checkersBoard;
    }


    /**
     * beigeHasWon method
     * checks if black has won
     */
    public boolean gameisOver() {
        // X Diagonal Win
        if (blackcheckers == 0 || beigecheckers == 0) {
            gameover = true;
        }

        return gameover;
    }

    /**
     * beigeHasWon method
     * checks if black has won
     */
    public boolean beigeHasWon() {
        // X Diagonal Win
        if (blackcheckers == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * blackHasWon method
     * checks if black has won
     */

    public boolean blackHasWon() {
        // O Diagonal Win
        if (beigecheckers == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * getAvailableMoves - returns availablemoves - for use in gui class
     * @return availableMoves
     *
     */
    public ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>> getavailableMoves() {
        return availableMoves;
    }

    /**
     * getTurn - getter to identify who's turn it is
     * @return
     */
    public Colour getTurn() {
        return turn;
    }

    /**
     * setTurn - setter method
     * @param turn
     */
    public void setTurn(Colour turn) {
        this.turn = turn;
    }

    /**
     * move method - called when making a move
     * includes checking for multi leg jump, regicide -- decrements respective
     * checkers if they are jumped of. Also does multileg and changes turn accordingly
     *
     *
     * @param checker
     */
    public void move( int finishRow,int finishCol, Checkers checker) {
        checkersBoard[finishRow][finishCol] = checkersBoard[checker.getRow()][checker.getCol()];
        checkersBoard[checker.getRow()][checker.getCol()] = null;

        checker.setPos(finishRow, finishCol);

        ArrayList<Integer> pos = new ArrayList<>();
        pos.add(finishRow);
        pos.add(finishCol);

        for (HashMap<ArrayList<Integer>, ArrayList<Checkers>> nextmove : availableMoves) {
            //nextmove is your hashmap
            // hash map contains the key row, col array list
            // value is the arraylist of checkers or null
            //  new array list with the checker.getRow() and checker.getCol() in it
            //use arrayList of checkers and go through each checker in the list checking if the colour is beige
            if(nextmove.get(pos) != null) {
                //changing a normal tile to king after capturing a king
                if (nextmove.get(pos).get(0).getColour() == Colour.beige) {
                    if (nextmove.get(pos).get(0).getisKing()) {
                        checker.setisKing();
                    }
                    //no matter if king or not king is captured decrement blackcheckers
                    --blackcheckers;
                }
                //if beige - regicide specification
                else if (nextmove.get(pos).get(0).getColour() == Colour.black) {
                    if (nextmove.get(pos).get(0).getisKing()) {
                        checker.setisKing();
                    }
                    //decrement beige checkers if beigechecker is captured
                    --beigecheckers;
                }
            }
        }

        //multi leg jumps
        //if hashmap has size of greater than 1 --> multi leg
        if(availableMoves.get(0).get(pos) != null) {
            if (availableMoves.size() != count + 1) {
                for (Checkers chk : availableMoves.get(0).get(pos)) {
                    //get available positions for multileg jump
                    if (chk.getCol() > finishCol && chk.getRow() < finishRow) {
                        count += 1;
                    }
                }
            } else {
                count = 0;
                //move method also entails the necessary change in turn after a move is completed and no more
                //possible moves can be found
                changeTurn(pos);
            }
        }
        //change turn if no more move or multi leg capture is possible
        changeTurn(pos);

        //if the hashmaps size is equal to the count + 1 then break and swap turn
        //size 3 - count + 1

        //calling kingConversion in move method - if conditions for kingconversion are met - specific tile is changed to king
        //see method
        kingConversion(finishRow, finishCol);

    }

    /**
     * changeTurn method - changes turn and is called after each move
     * @param pos
     */
    public void changeTurn(ArrayList<Integer> pos) {
        //changing the turn and thus the colour which is able to complete movements
        //takes position parameter to identify colour of given tile
        if(checkersBoard[pos.get(0)][pos.get(1)].getColour() == Colour.black){
            turn = Colour.beige;
        }
        else if(checkersBoard[pos.get(0)][pos.get(1)].getColour() == Colour.beige){
            turn = Colour.black;
        }
        //available moves to null if turn has concluded no matter which colour of checker
        //ensures that the next move continues
        availableMoves = null;
    }


    /**
     * kingconversion method - converts king at respective final row of board
     *
     * @param finishRow
     * @param finishCol
     */
    public void kingConversion(int finishRow, int finishCol) {
        //if we're at bottom row of the board with a beige checker - change to king
        if (finishRow == 0 && checkersBoard[finishRow][finishCol].getColour() == Colour.beige) {
            checkersBoard[finishRow][finishCol].getisKing();
            //if we're at top of row with a black checker
        } else if (finishRow == 7 && checkersBoard[finishRow][finishCol].getColour() == Colour.black)
            checkersBoard[finishRow][finishCol].getisKing();
    }




    /**
     * possibleMoves - creates Arraylist of Hashmaps of Arrylist of Integers and ArrayList of Checkers. At first this might seem convuluted
     * but it helps in storing information about each respective field on the checkersboard. To make sure we check all possible
     * moves we then call the checkLeft and checkRight method given a certain colour and then specifiy in which way these
     * tiles can move. Beige(down), Black(up)
     * <p>
     * This method also includes forced capture as with the information about possible moves we can easily loop through
     * our possible moves and identify whether we need to continue jumping
     *
     * @return jumplist
     */
    public ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>> possibleMoves(Checkers checker) {
        //creating different lists - helps to append forcedcapture moves
        ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>> moves = new ArrayList<>();
        ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>> forcedcapture = new ArrayList<>();
        ArrayList<HashMap<ArrayList<Integer>, ArrayList<Checkers>>> force = new ArrayList<>();

        //if checker is of certain colour add possible moves - given the conditions specified in the checkLeft and checkRight method
        if (checker.colour == Colour.beige || checker.getisKing()) {
            //getting column and row of respective checker to add moves - identified in checkLeft and checkRight method
            moves.add(checkLeft(checker.getRow() - 1, Math.max(checker.getRow() - 3, -1), -1, checker.getCol() - 1, checker.getColour(), new ArrayList<>()));
            moves.add(checkRight(checker.getRow() - 1, Math.max(checker.getRow() - 3, -1), -1, checker.getCol() + 1, checker.getColour(), new ArrayList<>()));
        } else if (checker.colour == Colour.black || checker.getisKing()) {
            moves.add(checkLeft(checker.getRow() + 1, Math.min(checker.getRow() + 3, 8), 1, checker.getCol() - 1, checker.getColour(), new ArrayList<>()));
            moves.add(checkRight(checker.getRow() + 1, Math.min(checker.getRow() + 3, 8), 1, checker.getCol() + 1, checker.getColour(), new ArrayList<>()));

        }


        // forced capture
        for (int i = 0; i < moves.size(); i += 2) { //loop through moves if they are jumps
            force.addAll(moves);
            if (moves.size() <= 1) {
                //if no longer a jump return the moves
                return moves;
                //else return the forcedcapture and complete moves
            } else {
                forcedcapture = force;
            }
        }
            return forcedcapture;
    }





        /**
         * checkleft method - checks all potential left moves and stores them in  a board
         * convoluted data structures representing a HashMop of Arraylists of Integers and Arraylist of Checkers. Hashmap represents
         * location as key and its value as null or not. If piece is present the piece is shown, if not it shows null. Multiple arraylists are then used to
         * store the different information from fields left of the current position and given certain conditions values are
         * added to the given arraylists.
         *
         * Finally we have a recursive method call to ensure that the method keeps on running until all possivle moves are
         * made (MultiLeg capture and forced capture)
         *
         * This method also covers the conditional aspects of the baord class and checks if all moves made are valid. It treats
         * the values in the board as parts of the hash map and can thus easily
         *
         * @param start
         * @param stop,
         * @param increment
         * @param left,
         * @param colour,
         * @param jumped
         *
         */
        private HashMap<ArrayList<Integer>, ArrayList<Checkers>> checkLeft ( int start, int stop, int increment,
        int left, Colour colour, ArrayList < Checkers > jumped){
            //new hashmap - which is later added to the possible moves method - to be called whenever a player evaluates
            //the next possible move they can make
            HashMap<ArrayList<Integer>, ArrayList<Checkers>> movesLeft = new HashMap<>();
            ArrayList<Checkers> last = new ArrayList<>(); //last position before moving

            if (colour == Colour.black) {
                //change row > stop specification depending on colour
                //allows accurate jumps to be made anc clicked in the UI -
                for (int row = start; row < stop; row += increment) { //loop through possible moves - increment row as moving along
                    if (left < 0) //bottom of board
                        break;

                    if (fieldtile(row, left) == null) {
                        // continue as normal

                        if (!jumped.isEmpty() && last.isEmpty()) {
                            break;
                        } else if (!jumped.isEmpty()) {
                            // this means we have all the jumps for this one possible move
                            ArrayList<Integer> pos = new ArrayList<>();
                            //checkers array - stores checkers
                            ArrayList<Checkers> allJumps = new ArrayList<>();
                            pos.add(row);
                            pos.add(left);
                            //add all checkers to allJumps
                            allJumps.addAll(last);
                            allJumps.addAll(jumped);
                            movesLeft.put(pos, allJumps);
                        } else {
                            //Integer Array - represents key in hashmap of arraylists
                            ArrayList<Integer> pos = new ArrayList<>();
                            pos.add(row);
                            pos.add(left);
                            movesLeft.put(pos, null);
                        }

                        //if Arraylist of checkers is empty calculate
                        if (!last.isEmpty()) { // if last is not empty
                            //calculate Min and Max to identify which next field can be moved to
                            //Min - identifies smaller of two ints, max larger
                            int calc = 0;
                            if (increment == -1) {
                                calc = Math.max(row - 3, 0);
                            } else {
                                calc = Math.min(row + 3, 8);
                            }
                            //recursive approach
                            //compare hash maps to make accurate multi leg jumps and captures
                            //compare old hash with new potential moves to the left

                            //specifies move to the left and to the right after first step was to the left
                            checkLeft(row + increment, calc, increment, left - 1, colour, last);
                            checkRight(row + increment, calc, increment, left + 1, colour, last);
                            //movesLeft.clear();

//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesLeft = checkLeft(row + increment, calc, increment, left - 1, colour, last);
//                            compareHash(movesLeft,newMovesLeft);
//
//                            //compare old hash with new potential moves to the right
//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesRight = checkRight(row + increment, calc, increment, left + 1, colour, last);
//                            compareHash(movesLeft,newMovesRight);
                        }
                        break;

                    } else if (fieldtile(row, left).getColour() == colour) {
                        break;
                    } else {
                        last.add(fieldtile(row, left));
                    }
                    left--; //decrement left
                }
            } else {
                //if beige - for loop changes
                for (int row = start; row > stop; row += increment) { //loop through possible moves - increment row as moving along
                    if (left < 0) //bottom of board
                        break;

                    if (fieldtile(row, left) == null) {
                        // continue as normal

                        if (!jumped.isEmpty() && last.isEmpty()) {
                            break;
                        } else if (!jumped.isEmpty()) {
                            // this means we have all the jumps for this one possible move
                            ArrayList<Integer> pos = new ArrayList<>();
                            //checkers array - stores checkers
                            ArrayList<Checkers> allJumps = new ArrayList<>();
                            pos.add(row);
                            pos.add(left);
                            //add all checkers to allJumps
                            allJumps.addAll(last);
                            allJumps.addAll(jumped);
                            movesLeft.put(pos, allJumps);
                        } else {
                            //Integer Array - represents key in hashmap of arraylists
                            ArrayList<Integer> pos = new ArrayList<>();
                            pos.add(row);
                            pos.add(left);
                            movesLeft.put(pos, null);
                        }

                        //if Arraylist of checkers is empty calculate
                        if (!last.isEmpty()) { // if last is not empty
                            //calculate Min and Max to identify which next field can be moved to
                            //Min - identifies smaller of two ints, max larger
                            int calc = 0;
                            if (increment == -1) {
                                calc = Math.max(row - 3, 0);
                            } else {
                                calc = Math.min(row + 3, 8);
                            }

                            checkLeft(row + increment, calc, increment, left - 1, colour, last);
                            checkRight(row + increment, calc, increment, left + 1, colour, last);

                            //recursive approach

//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesLeft = checkLeft(row + increment, calc, increment, left - 1, colour, last);
//                            compareHash(movesLeft,newMovesLeft);
//
//                            //compare old hash with new potential moves to the right
//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesRight = checkRight(row + increment, calc, increment, left + 1, colour, last);
//                            compareHash(movesLeft,newMovesRight);

                        }
                        break;

                    } else if (fieldtile(row, left).getColour() == colour) {
                        break;
                    } else {
                        last.add(fieldtile(row, left));
                    }
                    left--; //decrement left
                }
            }

            return movesLeft;
        }


        /**
         * checkright method - identical to checkleft method but instead checks all potential right moves and stores them in  a board
         * convoluted data structures representing a HashMop of Arraylists of Integers and Arraylist of Checkers. Hashmap represents
         * location as key and its value as null or not. If piece is present the piece is shown, if not it shows null. Multiple arraylists are then used to
         * store the different information from fields left of the current position and given certain conditions values are
         * added to the given arraylists. This method as well as checkleft are later called in the possible moves method to check
         * if certain moves can be made are not.
         *
         * Finally we have a recursive method call to ensure that the method keeps on running until all possivle moves are
         * made (MultiLeg capture and forced capture)
         *
         * @param start
         * @param stop,
         * @param increment
         * @param right,
         * @param colour,
         * @param jumped
         *
         */
        private HashMap<ArrayList<Integer>, ArrayList<Checkers>> checkRight ( int start, int stop, int increment,
        int right, Colour colour, ArrayList < Checkers > jumped){
            HashMap<ArrayList<Integer>, ArrayList<Checkers>> movesRight = new HashMap<>();
            ArrayList<Checkers> last = new ArrayList<>(); //last position before moving

            if (colour == Colour.black) {
                //change row > stop specification depending on colour
                //allows accurate jumps to be made anc clicked in the UI -
                for (int row = start; row < stop; row += increment) { //loop through possible moves - increment row as moving along
                    if (right >= 8)
                        break;
                    //if we at top of board - break

                    if (fieldtile(row, right) == null) {
                        // continue as normal

                        if (!jumped.isEmpty() && last.isEmpty()) {
                            break;
                        } else if (!jumped.isEmpty()) {
                            // this means we have all the jumps for this one possible move
                            ArrayList<Integer> pos = new ArrayList<>();
                            //checkers array
                            ArrayList<Checkers> allJumps = new ArrayList<>();
                            pos.add(row);
                            pos.add(right);
                            //add all checkers to allJumps
                            allJumps.addAll(last);
                            allJumps.addAll(jumped);
                            movesRight.put(pos, allJumps);
                        } else {
                            //Integer array - represents key in hashmap
                            ArrayList<Integer> pos = new ArrayList<>();
                            pos.add(row);
                            pos.add(right);
                            movesRight.put(pos, null);
                        }

                        if (!last.isEmpty()) { // if last is not empty
                            //calculate Min and Max to identify which next field can be moved to
                            //Min - identifies smaller of two ints, max larger
                            int calc = 0;
                            if (increment == -1) {
                                calc = Math.max(row - 3, 0);
                            } else {
                                calc = Math.min(row + 3, 8);
                            }
                            //recursive approach

                            //check direction to take after
                            checkLeft(row + increment, calc, increment, right - 1, colour, last);
                            checkRight(row + increment, calc, increment, right + 1, colour, last);

//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesLeft = checkLeft(row + increment, calc, increment, right - 1, colour, last);
//                            compareHash(movesRight,newMovesLeft);
//
//                            //compare old hash with new potential moves to the right
//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesRight = checkRight(row + increment, calc, increment, right + 1, colour, last);
//                            compareHash(movesRight,newMovesRight);

                        }
                        break;
                        //if already has a colour - own team colour don't continue
                    } else if (fieldtile(row, right).getColour() == colour) {
                        break;
                    } else {
                        last.add(fieldtile(row, right));
                    }
                    right++; //increment right
                }
            } else {
                //if beige - for loop changes
                for (int row = start; row > stop; row += increment) { //loop through possible moves - increment row as moving along
                    if (right >= 8)
                        break;
                    //if we at top of board - break

                    if (fieldtile(row, right) == null) {
                        // continue as normal

                        if (!jumped.isEmpty() && last.isEmpty()) {
                            break;
                        } else if (!jumped.isEmpty()) {
                            // this means we have all the jumps for this one possible move
                            ArrayList<Integer> pos = new ArrayList<>();
                            //checkers array
                            ArrayList<Checkers> allJumps = new ArrayList<>();
                            pos.add(row);
                            pos.add(right);
                            //add all checkers to allJumps
                            allJumps.addAll(last);
                            allJumps.addAll(jumped);
                            movesRight.put(pos, allJumps);
                        } else {
                            //Integer array - represents key in hashmap
                            ArrayList<Integer> pos = new ArrayList<>();
                            pos.add(row);
                            pos.add(right);
                            movesRight.put(pos, null);
                        }

                        if (!last.isEmpty()) { // if last is not empty
                            //calculate Min and Max to identify which next field can be moved to
                            //Min - identifies smaller of two ints, max larger
                            int calc = 0;
                            if (increment == -1) {
                                calc = Math.max(row - 3, 0);
                            } else {
                                calc = Math.min(row + 3, 8);
                            }
                            //recursive approach
                            checkLeft(row + increment, calc, increment, right - 1, colour, last);
                            checkRight(row + increment, calc, increment, right + 1, colour, last);
                            //recursive approach
//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesLeft = checkLeft(row + increment, calc, increment, right - 1, colour, last);
//                            compareHash(movesRight,newMovesLeft);
//
//                            //compare old hash with new potential moves to the right
//                            HashMap<ArrayList<Integer>, ArrayList<Checkers>> newMovesRight = checkRight(row + increment, calc, increment, right + 1, colour, last);
//                            compareHash(movesRight,newMovesRight);
                        }
                        break;
                        //if already has a colour - own team colour don't continue
                    } else if (fieldtile(row, right).getColour() == colour) {
                        break;
                    } else {
                        last.add(fieldtile(row, right));
                    }
                    right++; //increment right
                }
            }

            return movesRight;
        }

//    /**
//     * method to compare hashmaps
//     * @param hash1
//     * @param hash2
//     * @return
//     */
//        public HashMap<ArrayList<Integer>, ArrayList<Checkers>> compareHash(HashMap<ArrayList<Integer>, ArrayList<Checkers>> hash1,HashMap<ArrayList<Integer>, ArrayList<Checkers>> hash2){
//
//            Iterator hmIter = hash1.entrySet().iterator();
//            for (Map.Entry element : hash1.entrySet()){
//                hash2.replace(element,value);
//            }
//            return hash2;
//        }


}




/**
 * Jump method to recognise if jump was made - old method before checkleft and checkright method was made
 * Replaced by checkleft and checkright and possiblemoves
 * while more simple than the hash map data structure covers less functionality but still correctly validates moves
 *
 * @param checker.getRow()
 * @param finishRow
 * @param startCol
 * @param finishCol
 * @return true
 */
//    public boolean checkMove(int checker.getRow(), int startCol, int finishRow, int finishCol) {
//        if (checkersBoard[finishRow][finishCol] != null) {
//            return false;
//        }
//        if (finishRow < 0 || finishRow >= 8 || finishCol < 0 || finishCol >= 8) {
//            return false;
//        }
//
//        if (checkersBoard[checker.getRow()][startCol].getColour() == Colour.beige) {
//            if (checker.getRow() - finishRow == 1 && startCol - finishCol == 1 || startCol - finishCol == -1) {
//                return true;
//
//            } else {
//                return false;
//            }
//
//        }
//
//        if (checkersBoard[checker.getRow()][startCol].getColour() == Colour.black) {
//            if (checker.getRow() + finishRow == 1 && startCol + finishCol == 1 || startCol + finishCol == +1) {
//                return true;
//
//            } else {
//                return false;
//            }
//
//        }
//        return true;
//    }
//
//
/**
 * Jump method to recognise if jump was made - old method before checkleft and checkright method
 * Replaced by checkleft and checkright and possiblemoves
 * while more simple than the hash map data structure covers less functionality but still correctly validates jumps
 *
 * @param checker.getRow()
 * @param startCol
 * @param finishRow
 * @param finishCol
 * @return false
 */
//    public boolean checkJump(int checker.getRow(), int startCol, int finishRow, int finishCol) {
//        if (checkersBoard[finishRow][finishCol] != null) {
//            return false;
//        }
//        if (finishRow < 0 || finishRow >= 8 || finishCol < 0 || finishCol >= 8) {
//            return false;
//        }
//
//        if (checkersBoard[checker.getRow()][startCol].getColour() == Colour.beige || checkersBoard[checker.getRow()][startCol].getisKing()) {
//            if (checker.getRow() - finishRow == 2) {
//                if (startCol - finishCol == 2) {
//                    return checkersBoard[checker.getRow() + 1][startCol + 1].getColour() == Colour.black;
//
//                } else if (startCol - finishCol == -2) {
//                    return checkersBoard[checker.getRow() + 1][startCol - 1].getColour() == Colour.black;
//
//                } else if (checkersBoard[checker.getRow()][startCol].getColour() == Colour.black == checkersBoard[checker.getRow()][startCol].getisKing()) {
//                    return checkersBoard[checker.getRow()][startCol].getisKing();
//                }
//            }
//        }
//
//        if (checkersBoard[checker.getRow()][startCol].getColour() == Colour.black || checkersBoard[checker.getRow()][startCol].getisKing()) {
//            if (checker.getRow() + finishRow == 2) {
//                if (startCol + finishCol == 2) {
//                    return checkersBoard[checker.getRow() - 1][startCol - 1].getColour() == Colour.beige;
//
//                } else if (startCol + finishCol == +2) {
//                    return checkersBoard[checker.getRow() - 1][startCol + 1].getColour() == Colour.beige;
//
//                } else if (checkersBoard[checker.getRow()][startCol].getColour() == Colour.beige == checkersBoard[checker.getRow()][startCol].getisKing()) {
//                    return checkersBoard[checker.getRow()][startCol].getisKing();
//                }
//            }
//
//        }
//
//        return false;
//
//    }




//
//System.out.println("You can't make this jump. Capture the enemy checker");
//System.out.println("You can't make this move. Capture the enemy checker");
//System.out.println("You can't make this jump. You can only move diagonally");
//System.out.println("You can't make this move. You can only move diagonally");




