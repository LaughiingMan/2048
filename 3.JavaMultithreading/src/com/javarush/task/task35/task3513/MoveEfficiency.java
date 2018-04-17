package com.javarush.task.task35.task3513;

/**
 * Created by 777 on 11.04.2018.
 */
public class MoveEfficiency implements Comparable<MoveEfficiency> {

    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() { return move; }

    @Override
    public int compareTo(MoveEfficiency o) {

        if(this == o) return 0;

        if(this.numberOfEmptyTiles > o.numberOfEmptyTiles) return 1;
        if(this.numberOfEmptyTiles < o.numberOfEmptyTiles) return -1;

        if(this.numberOfEmptyTiles == o.numberOfEmptyTiles) {
            if(this.score > o.score) return 1;
            if(this.score < o.score) return -1;
        }
        return 0;
    }
}
