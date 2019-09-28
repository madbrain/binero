package com.github.madbrain.binero;

@FunctionalInterface
interface CellFunction {
    boolean onCell(BineroCell cell, int i, int j);
}
