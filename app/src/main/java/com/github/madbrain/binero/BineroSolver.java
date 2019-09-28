package com.github.madbrain.binero;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

public class BineroSolver {

    public BineroGrid solve(BineroGrid grid) {
        Random random = new Random();
        Deque<BineroGrid> stack = new LinkedList<>();
        BineroGrid current = new BineroGrid(grid);
        while (!current.isFinished()) {
            if (solveOneStep(current)) {
                if (!check(current)) {
                    if (stack.size() > 0) {
                        current = stack.pop();
                    } else {
                        return null;
                    }
                }
            } else {
                if (stack.size() > 10000) {
                    return null;
                }
                // Really naive strategy, but works quite well
                int i = 0;
                int j = 0;
                boolean found = false;
                while (!found) {
                    i = random.nextInt(current.getSize());
                    j = random.nextInt(current.getSize());
                    if (!grid.isSet(i, j)) {
                        found = true;
                    }
                }
                BineroGrid other = new BineroGrid(current);
                stack.push(other);
                BineroCell v = random.nextFloat() < 0.5 ? BineroCell.ZERO : BineroCell.ONE;
                other.set(i, j, v);
                current.set(i, j, v.opposed());
            }
        }
        return current;
    }

    private boolean solveOneStep(BineroGrid current) {
        return current.every((c, i, j) -> c == BineroCell.EMPTY && inspecterRegles(current, i, j))
                || checkAxe(current, current::line)
                || checkAxe(current, current::column);
    }

    private boolean checkAxe(BineroGrid grid, UpdaterAccessor updaterProvider) {
        boolean changed = false;
        final int midSize = grid.getSize() / 2;
        for (int i = 0; i < grid.getSize(); ++i) {
            BineroGrid.CellUpdater updater = updaterProvider.get(i);
            CountResult result = grid.count(updater);
            if (result.count0 == midSize || result.count1 == midSize) {
                changed |= grid.complete(result.count0 == midSize ? BineroCell.ONE : BineroCell.ZERO, updater);
            }
        }
        return changed;
    }

    @FunctionalInterface
    interface UpdaterAccessor {
        BineroGrid.CellUpdater get(int i);
    }

    private boolean check(BineroGrid grid) {
        int midSize = grid.getSize() / 2;
        for (int i = 0; i < grid.getSize(); ++i) {
            for (BineroGrid.CellGetter getter : Arrays.asList(grid.line(i), grid.column(i))) {
                CountResult result = grid.count(getter);
                if (result.count0 > midSize || result.count1 > midSize) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean inspecterRegles(BineroGrid grid, int i, int j) {
        boolean changed = false;
        // Régle du double
        if (j + 2 < grid.getSize()) {
            if (grid.isSet(i, j + 1) && grid.get(i, j + 1) == grid.get(i, j + 2)) {
                grid.set(i, j, grid.get(i, j + 1).opposed());
                changed = true;
            }
        }
        if (j >= 2) {
            if (grid.isSet(i, j - 1) && grid.get(i, j - 1) == grid.get(i, j - 2)) {
                grid.set(i, j, grid.get(i, j - 1).opposed());
                changed = true;
            }
        }
        if (i + 2 < grid.getSize()) {
            if (grid.isSet(i + 1, j) && grid.get(i + 1, j) == grid.get(i + 2, j)) {
                grid.set(i, j, grid.get(i + 1, j).opposed());
                changed = true;
            }
        }
        if (i >= 2) {
            if (grid.isSet(i - 1, j) && grid.get(i - 1, j) == grid.get(i - 2, j)) {
                grid.set(i, j, grid.get(i - 1, j).opposed());
                changed = true;
            }
        }
        // Régle de la séparation
        if (j >= 1 && j + 1 < grid.getSize()) {
            if (grid.isSet(i, j + 1) && grid.get(i, j + 1) == grid.get(i, j - 1)) {
                grid.set(i, j, grid.get(i, j + 1).opposed());
                changed = true;
            }
        }
        if (i >= 1 && i + 1 < grid.getSize()) {
            if (grid.isSet(i + 1, j) && grid.get(i + 1, j) == grid.get(i - 1, j)) {
                grid.set(i, j, grid.get(i + 1, j).opposed());
                changed = true;
            }
        }
        return changed;
    }

}
