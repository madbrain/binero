package com.github.madbrain.binero;

import java.util.Random;

public class BineroGrid {

    private BineroCell[][] content;

    public BineroGrid() {
        this(10);
    }

    public BineroGrid(int size) {
        this.content = new BineroCell[size][size];
        Random random = new Random();
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                this.content[i][j] = BineroCell.EMPTY; // BineroCell.values()[random.nextInt(3)];
            }
        }
        for (int i = 0; i < size; ++i) {
            this.content[i][i] = BineroCell.ONE;
        }
        this.content[1][2] = BineroCell.ZERO;
        this.content[2][1] = BineroCell.ZERO;
    }
    
    public int getSize() {
        return this.content.length;
    }
    
    public BineroCell get(int i, int j) {
        return this.content[i][j];
    }

    public void cycle(int i, int j) {
        this.content[i][j] = cycleCell(this.content[i][j]);
    }

    private BineroCell cycleCell(BineroCell cell) {
        return BineroCell.values()[(cell.ordinal() + 1) % BineroCell.values().length];
    }
}
