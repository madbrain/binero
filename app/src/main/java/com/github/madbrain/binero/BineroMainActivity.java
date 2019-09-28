package com.github.madbrain.binero;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class BineroMainActivity extends AppCompatActivity implements
        ChangeSizeFragment.ChangeSizeListener, BineroView.Listener {

    private BineroGrid grid;
    private BineroGrid solution;
    private BineroView bineroView;
    private ViewMode mode = ViewMode.HINT;
    private Selection selection = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.main_toolbar));

        bineroView = findViewById(R.id.binero_view);
        bineroView.addListener(this);
        setGrid(new BineroGrid());
    }

    private void setGrid(BineroGrid grid) {
        this.grid = grid;
        bineroView.setGrid(grid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mode == ViewMode.HINT) {
            getMenuInflater().inflate(R.menu.hint, menu);
        } else {
            getMenuInflater().inflate(R.menu.edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                bineroView.resetViewport();
                break;

            case R.id.action_size:
                ChangeSizeFragment changeSizeFragment = ChangeSizeFragment.make(grid.getSize());
                changeSizeFragment.show(getFragmentManager(), "changeSize");
                break;

            case R.id.action_edit:
                setMode(ViewMode.EDIT);
                invalidateOptionsMenu();
                break;

            case R.id.action_save:
                setMode(ViewMode.HINT);
                invalidateOptionsMenu();
                solution = new BineroSolver().solve(grid);
                if (solution == null) {
                    Toast.makeText(this, "Invalid grid", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.action_scan:
                Toast.makeText(this, "Scan", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return true;
    }

    private void setMode(ViewMode mode) {
        this.mode = mode;
        this.bineroView.setMode(mode);
    }

    @Override
    public void onChangeSize(int newSize) {
        if (newSize % 2 == 0 && newSize >= 6 && newSize <= 20) {
            setGrid(new BineroGrid(newSize));
        }
    }

    @Override
    public void onSelectPoint(int i, int j) {
        if (mode == ViewMode.EDIT) {
            grid.cycle(i, j);
            bineroView.invalidate();
        } else {
            if (solution != null && selection != null && selection.isAt(i, j)) {
                this.grid.set(i, j, this.solution.get(i, j));
                bineroView.invalidate();
                selection = null;
            } else {
                selection = new Selection(i, j);
            }
        }
    }

    @Override
    public void onDeselectPoint() {

    }

}

