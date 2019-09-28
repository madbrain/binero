package com.github.madbrain.binero;

public class Selection {
    private final int i;
    private final int j;

    public Selection(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public boolean isAt(int i, int j) {
        return this.i == i && this.j == j;
    }
}
