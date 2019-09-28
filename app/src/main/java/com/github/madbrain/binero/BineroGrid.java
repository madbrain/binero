package com.github.madbrain.binero;

public class BineroGrid {

    private BineroCell[][] content;

    public BineroGrid() {
        this(10);
    }

    public BineroGrid(int size) {
        this.content = new BineroCell[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                this.content[i][j] = BineroCell.EMPTY;
            }
        }
    }

    public BineroGrid(BineroGrid grid) {
        this.content = new BineroCell[grid.getSize()][grid.getSize()];
        for (int i = 0; i < getSize(); ++i) {
            for (int j = 0; j < getSize(); ++j) {
                this.content[i][j] = grid.get(i, j);
            }
        }
    }

    public int getSize() {
        return this.content.length;
    }

    public BineroCell get(int i, int j) {
        return this.content[i][j];
    }

    public boolean isSet(int i, int j) {
        return get(i, j) != BineroCell.EMPTY;
    }

    public void set(int i, int j, BineroCell cell) {
        this.content[i][j] = cell;
    }

    public void cycle(int i, int j) {
        this.content[i][j] = cycleCell(this.content[i][j]);
    }

    private BineroCell cycleCell(BineroCell cell) {
        return BineroCell.values()[(cell.ordinal() + 1) % BineroCell.values().length];
    }

    public boolean isFinished() {
        EmptyCellCount func = new EmptyCellCount();
        this.every(func);
        return func.emptyCount == 0;
    }

    public CountResult count(CellGetter func) {
        int count0 = 0;
        int count1 = 0;
        for (int i = 0; i < getSize(); ++i) {
            if (func.getCell(i) == BineroCell.ZERO) {
                count0++;
            } else if (func.getCell(i) == BineroCell.ONE) {
                count1++;
            }
        }
        return new CountResult(count0, count1);
    }

    public boolean complete(BineroCell cell, CellUpdater cellupdater) {
        boolean changed = false;
        for (int i = 0; i < getSize(); ++i) {
            if (cellupdater.getCell(i) == BineroCell.EMPTY) {
                cellupdater.set(i, cell);
                changed = true;
            }
        }
        return changed;
    }

    public CellUpdater line(int i) {
        return new LineUpdater(i);
    }

    public CellUpdater column(int j) {
        return new ColumnUpdater(j);
    }

    public interface CellGetter {
        BineroCell getCell(int i);
    }

    public interface CellUpdater extends CellGetter {
        void set(int i, BineroCell cell);
    }

    private class ColumnUpdater implements CellUpdater {

        private int column;

        public ColumnUpdater(int column) {
            this.column = column;
        }

        @Override
        public BineroCell getCell(int i) {
            return BineroGrid.this.get(i, column);
        }

        @Override
        public void set(int i, BineroCell cell) {
            BineroGrid.this.set(i, column, cell);
        }
    }

    private class LineUpdater implements CellUpdater {

        private int line;

        public LineUpdater(int line) {
            this.line = line;
        }

        @Override
        public BineroCell getCell(int i) {
            return BineroGrid.this.get(line, i);
        }

        @Override
        public void set(int i, BineroCell cell) {
            BineroGrid.this.set(line, i, cell);
        }
    }

    private class EmptyCellCount implements CellFunction {
        public int emptyCount = 0;

        @Override
        public boolean onCell(BineroCell cell, int i, int j) {
            if (cell == BineroCell.EMPTY) {
                emptyCount++;
            }
            return false;
        }
    }

    public boolean every(CellFunction func) {
        for (int i = 0; i < getSize(); ++i) {
            for (int j = 0; j < getSize(); ++j) {
                if (func.onCell(get(i, j), i, j)) {
                    return true;
                }
            }
        }
        return false;
    }
}
