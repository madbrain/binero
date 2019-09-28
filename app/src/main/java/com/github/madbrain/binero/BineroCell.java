package com.github.madbrain.binero;

public enum BineroCell {
    ZERO,
    ONE,
    EMPTY;

    public BineroCell opposed() {
        return this == ZERO ? ONE : this == ONE ? ZERO : EMPTY;
    }
}
