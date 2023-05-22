/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.cw2kr;

/**
 * @author 215732
 * <p>
 * The checkersSet class provides information on the respective pieces of both
 * players. This is then further utilised in
 */
public class Checkers {


    public boolean isKing;
    public Colour colour;
    public int row;
    public int col;


    /**
     * Constructor
     * Initializes the boolean isKing as false, as game starts with only normal
     * Checkers and no King.
     */
    public Checkers() {

        isKing = false;
    }


    /**
     * getisKing() method - getter method
     * returns if a piece is King or not
     */
    public boolean getisKing() {
        return isKing;

    }


    /**
     * setisKing method - setter method
     * changes the piece to a King
     */
    public void setisKing() {
        this.isKing = true;

    }


    /**
     * getColour method - refers to enum
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * setColour
     * refers to enum
     */
    public void setColour(Colour colour) {
        this.colour = colour;
    }


    /**
     * setPos method - sets row and coloum
     * covers position of checker - row and column specification
     */
    public void setPos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * getRow - getter for row value
     */
    public int getRow() {
        return row;
    }

    /**
     * getCol - getter for col value
     */
    public int getCol() {
        return col;
    }
}
