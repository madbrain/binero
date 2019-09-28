package com.github.madbrain.binero;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

/**
 * https://developer.android.com/guide/topics/ui/dialogs#DialogFragment
 */
public class ChangeSizeFragment extends DialogFragment {

    public interface ChangeSizeListener {
        void onChangeSize(int newSize);
    }

    private static final String SIZE_ARG = "size";

    private ChangeSizeListener listener;
    private int currentSize;

    public static ChangeSizeFragment make(int size) {
        ChangeSizeFragment f = new ChangeSizeFragment();
        Bundle args = new Bundle();
        args.putInt(SIZE_ARG, size);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentSize = getArguments().getInt(SIZE_ARG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ChangeSizeListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // TODO should be replace with fixed size list
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setText(String.valueOf(currentSize));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_change_size)
                .setView(input)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    listener.onChangeSize(Integer.parseInt(input.getText().toString()));
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // do nothing
                })
                .create();
    }

}
