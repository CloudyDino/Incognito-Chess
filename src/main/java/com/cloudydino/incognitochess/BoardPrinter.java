package com.cloudydino.incognitochess;

abstract class BoardPrinter {

    protected Board board;

    BoardPrinter(Board board) {
        this.board = board;
    }

    abstract void printBoard();
}
