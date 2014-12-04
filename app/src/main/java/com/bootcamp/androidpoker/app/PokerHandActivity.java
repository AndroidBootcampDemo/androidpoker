package com.google.android.bootcamp.poker;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by freopen on 12/4/14.
 */
public class PokerHandActivity extends android.support.v4.app.FragmentActivity {
    public ClientCommunicator communicator;
    public int currentCash = 152;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_hand);

        communicator = new ClientCommunicator() {
            @Override
            void onShowCards(String first, String second) {
                ImageView handFirst = (ImageView) findViewById(R.id.hand_first);
                int firstId = getResources().getIdentifier("card_" + first, "drawable", getPackageName());
                handFirst.setImageResource(firstId);

                ImageView handSecond = (ImageView) findViewById(R.id.hand_second);
                int secondId = getResources().getIdentifier("card_" + second, "drawable", getPackageName());
                handSecond.setImageResource(secondId);
            }

            @Override
            void onHideCards() {
                ImageView handFirst = (ImageView) findViewById(R.id.hand_first);
                handFirst.setImageResource(R.drawable.card_blue_back);

                ImageView handSecond = (ImageView) findViewById(R.id.hand_second);
                handSecond.setImageResource(R.drawable.card_blue_back);
            }

            @Override
            void onChangeCash(int cash) {
                currentCash = cash;
                TextView cashValue = (TextView) findViewById(R.id.cash_value);
                cashValue.setText("$" + currentCash);
            }
        };

        Button callButton = (Button) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.call();
            }
        });

        Button raiseButton = (Button) findViewById(R.id.raise_button);
        raiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        Button foldButton = (Button) findViewById(R.id.fold_button);
        foldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.fold();
            }
        });
    }


    void showDialog() {
        FragmentManager manager = getSupportFragmentManager();
        RaiseSliderFragment raiseSliderFragment = new RaiseSliderFragment();
        raiseSliderFragment.show(manager, "dialog");
    }

}