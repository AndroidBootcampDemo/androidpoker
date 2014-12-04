package com.bootcamp.androidpoker.app;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by freopen on 12/4/14.
 */
public class RaiseSliderFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_raise_slider, container, false);
        getDialog().setTitle("Select raise amount");

        final PokerHandActivity activity = (PokerHandActivity) getActivity();
        final SeekBar raiseAmountBar = (SeekBar) view.findViewById(R.id.raise_amount_bar);
        final TextView raiseAmountLabel = (TextView) view.findViewById(R.id.raise_amount_label);

        raiseAmountBar.setMax(activity.currentCash - 1);
        raiseAmountBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                raiseAmountLabel.setText("$" + (raiseAmountBar.getProgress() + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button okButton = (Button) view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.communicator.raise(raiseAmountBar.getProgress() + 1);
                final Button callButton = (Button) activity.findViewById(R.id.call_button);
                final Button raiseButton = (Button) activity.findViewById(R.id.raise_button);
                final Button foldButton = (Button) activity.findViewById(R.id.fold_button);
                callButton.setEnabled(false);
                raiseButton.setEnabled(false);
                foldButton.setEnabled(false);
                dismiss();
            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }


}