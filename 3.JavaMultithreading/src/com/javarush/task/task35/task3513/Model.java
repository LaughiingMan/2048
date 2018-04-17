package com.javarush.task.task35.task3513;

import java.util.*;

/**
 * Created by 777 on 07.04.2018.
 */
public class Model {

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score = 0;
    int maxTile = 2;
    private Stack<Integer> previousScores = new Stack<>();
    private Stack<Tile[][]> previousStates = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {

        resetGameTiles();
    }

    public Tile[][] getGameTiles() { return gameTiles; }

    public boolean canMove() {

        if(!getEmptyTiles().isEmpty()) return true;

        for(int i = 0; i < gameTiles.length; i++) {
            for(int j = 0; j < gameTiles[i].length - 1; j++) {
                if (gameTiles[i][j].value != 0 && gameTiles[i][j + 1].value == gameTiles[i][j].value) {
                    return true;
                }
            }
        }

        for(int i = 0; i < gameTiles.length; i++) {
            for(int j = 0; j < gameTiles[i].length - 1; j++) {
                if (gameTiles[j][i].value != 0 && gameTiles[j + 1][i].value == gameTiles[j][i].value) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void resetGameTiles() {

        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i < FIELD_WIDTH; i++) {
            for(int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private List<Tile> getEmptyTiles() {

        List<Tile> resultEmpty = new ArrayList<>();

        for(int i = 0; i < gameTiles.length; i++) {
            for(int j = 0; j < gameTiles[i].length; j++) {
                if(gameTiles[i][j].isEmpty()) {
                    resultEmpty.add(gameTiles[i][j]);
                }
            }
        }

        return resultEmpty;
    }

    private void addTile() {

        List<Tile> emptyList = getEmptyTiles();
        if(emptyList != null && emptyList.size() != 0) {
            Tile tile = emptyList.get((int) (emptyList.size() * (Math.random() * 1)));
            for (int i = 0; i < gameTiles.length; i++) {
                for (int j = 0; j < gameTiles[i].length; j++) {
                    if (gameTiles[i][j].equals(tile)) {
                        gameTiles[i][j].setValue(Math.random() < 0.9 ? 2 : 4);
                    }
                }
            }
        }
    }

    private boolean compressTiles(Tile[] tiles) {

        boolean change = false;
        for(int i = 0; i < tiles.length; i++) {
            if(tiles[i].value == 0 && i < tiles.length - 1 && tiles[i + 1].value != 0) {
                Tile temp = tiles[i];
                tiles[i] = tiles[i + 1];
                tiles[i + 1] = temp;
                i = -1;
                change = true;
            }
        }

        return change;
    }

    private boolean mergeTiles(Tile[] tiles) {

        boolean change = false;
        for(int i = 0; i < tiles.length - 1; i++) {
            if(tiles[i].value != 0 && tiles[i + 1].value == tiles[i].value) {
                if(tiles[i].value + tiles[i + 1].value > maxTile) {
                    maxTile = tiles[i].value + tiles[i + 1].value;
                }
                tiles[i].value = tiles[i].value + tiles[i + 1].value;
                score += tiles[i].value;
                tiles[i + 1].value = 0;
                change = true;
                compressTiles(tiles);
            }
        }

        return change;
    }

    private void rotate() {

        for (int k = 0; k < 2; k++) {
            for (int j = k; j < 3 - k; j++) {
                Tile tmp = gameTiles[k][j];
                gameTiles[k][j] = gameTiles[j][3 - k];
                gameTiles[j][3 - k] = gameTiles[3 - k][3 - j];
                gameTiles[3 - k][3 - j] = gameTiles[3 - j][k];
                gameTiles[3 - j][k] = tmp;
            }
        }
    }

    public void left() {

        if(isSaveNeeded) {
            saveState(this.gameTiles);
        }

        boolean isChanged = false;
        for(int i = 0; i < FIELD_WIDTH; i++) {
            if(compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                isChanged = true;
            }
        }

        isSaveNeeded = true;
        if(isChanged) addTile();
    }

    public void up() {

        saveState(this.gameTiles);
        rotate();
        left();
        rotate();
        rotate();
        rotate();
    }

    public void right() {

        saveState(this.gameTiles);
        rotate();
        rotate();
        left();
        rotate();
        rotate();
    }

    public void down() {

        saveState(this.gameTiles);
        rotate();
        rotate();
        rotate();
        left();
        rotate();
    }

    public void randomMove() {

        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;

            case 1:
                right();
                break;

            case 2:
                up();
                break;

            case 3:
                down();
                break;
        }
    }

    private void saveState(Tile[][] temp) {

        Tile[][] saveTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i < temp.length; i++) {
            for(int j = 0; j < temp[i].length; j++) {
                saveTiles[i][j] = new Tile(temp[i][j].value);
            }
        }

        previousStates.push(saveTiles);
        int newScore = score;
        previousScores.push(newScore);
        isSaveNeeded = false;
    }

    public void rollback() {

        if(!previousStates.isEmpty()) {
            gameTiles = previousStates.pop();
        }

        if(!previousScores.isEmpty()) {
            score = previousScores.pop();
        }
    }

    public void autoMove() {

        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());
        priorityQueue.offer(getMoveEfficiency(this::left));
        priorityQueue.offer(getMoveEfficiency(this::right));
        priorityQueue.offer(getMoveEfficiency(this::up));
        priorityQueue.offer(getMoveEfficiency(this::down));
        
        priorityQueue.peek().getMove().move();
    }

    public boolean hasBoardChanged() {

        for (int i = 0; i < gameTiles.length; i++) {
            for(int j = 0; j < gameTiles[i].length; j++) {
                if(gameTiles[i][j].value != previousStates.peek()[i][j].value) {
                    return true;
                }
            }
        }

        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {

        move.move();
        MoveEfficiency moveEff;
        if(hasBoardChanged()) moveEff = new MoveEfficiency(getEmptyTiles().size(),score,move);
        else moveEff = new MoveEfficiency(-1,0,move);
        rollback();

        return moveEff;
    }
}
