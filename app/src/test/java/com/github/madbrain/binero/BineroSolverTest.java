package com.github.madbrain.binero;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class BineroSolverTest {

    private static final int[][] PAGE_1 = {
            {2, 1, 2, 2, 2, 1, 1, 2, 0, 2},
            {2, 2, 0, 2, 2, 1, 1, 2, 1, 2},
            {1, 1, 2, 2, 2, 2, 2, 2, 2, 2},
            {2, 2, 2, 2, 2, 1, 0, 2, 2, 1},
            {0, 0, 2, 1, 2, 2, 2, 2, 0, 1},
            {2, 2, 2, 2, 2, 0, 2, 2, 2, 2},
            {2, 2, 2, 2, 1, 2, 2, 0, 2, 0},
            {2, 2, 2, 2, 2, 2, 2, 1, 2, 2},
            {2, 0, 2, 1, 1, 2, 2, 2, 0, 2},
            {0, 1, 2, 1, 2, 2, 0, 2, 2, 2},
    };

    private static final int[][] PAGE_19 = {
            {2, 2, 2, 1, 2, 2, 2, 2, 2, 2},
            {0, 2, 2, 2, 2, 0, 2, 1, 2, 2},
            {2, 2, 1, 2, 1, 2, 2, 2, 2, 1},
            {2, 2, 2, 0, 2, 2, 2, 0, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 0, 2, 2},
            {2, 2, 2, 1, 2, 2, 2, 2, 1, 2},
            {2, 2, 2, 2, 2, 0, 2, 2, 2, 2},
            {0, 2, 2, 2, 2, 2, 1, 2, 2, 2},
            {0, 2, 2, 2, 2, 0, 2, 2, 2, 2},
            {2, 2, 0, 2, 2, 2, 2, 0, 2, 2},
    };

    @Test
    public void testSolveSimple() {
        BineroGrid grid = makeGrid(PAGE_1);
        BineroSolver solver = new BineroSolver();
        assertNotNull(solver.solve(grid));
    }

    @Test
    public void testSolveComplex() {
        BineroGrid grid = makeGrid(PAGE_19);
        BineroSolver solver = new BineroSolver();
        assertNotNull(solver.solve(grid));
    }

    private BineroGrid makeGrid(int[][] spec) {
        BineroGrid grid = new BineroGrid(spec.length);
        for (int i = 0; i < spec.length; ++i) {
            for (int j = 0; j < spec.length; ++j) {
                if (spec[i][j] != 2) {
                    grid.set(i, j, spec[i][j] == 1 ? BineroCell.ONE : BineroCell.ZERO);
                }
            }
        }
        return grid;
    }
}