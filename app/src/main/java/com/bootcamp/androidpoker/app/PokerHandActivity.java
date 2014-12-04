package com.bootcamp.androidpoker.app;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by freopen on 12/4/14.
 */
public class PokerHandActivity extends PokerActivity {
    public ClientCommunicator communicator;
    public int currentCash = 152;
    public int minBet;
    public int currentBet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_hand);

        String serverAddress = getIntent().getStringExtra(StartActivity.SERVER_ADDRESS_EXTRA);
        Toast.makeText(this, "game with server " + serverAddress, Toast.LENGTH_SHORT).show();

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

            @Override
            void onEnableActions(List<String> actions) {
                final Button callButton = (Button) findViewById(R.id.call_button);
                final Button raiseButton = (Button) findViewById(R.id.raise_button);
                final Button foldButton = (Button) findViewById(R.id.fold_button);
                callButton.setEnabled(false);
                raiseButton.setEnabled(false);
                foldButton.setEnabled(false);

                if (actions.contains("call")) {
                    callButton.setEnabled(true);
                    callButton.setText("Call");
                }
                if (actions.contains("check")) {
                    callButton.setEnabled(true);
                    callButton.setText("Check");
                }
                if (actions.contains("raise")) {
                    raiseButton.setEnabled(true);
                }
                if (actions.contains("fold")) {
                    foldButton.setEnabled(true);
                }

            }

            @Override
            void OnUpdateBetInfo(int minBet, int currentBet) {
                PokerHandActivity.this.minBet = minBet;
                PokerHandActivity.this.currentBet = currentBet;
            }
        };

        final Button callButton = (Button) findViewById(R.id.call_button);
        final Button raiseButton = (Button) findViewById(R.id.raise_button);
        final Button foldButton = (Button) findViewById(R.id.fold_button);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callButton.getText() == "Call") {
                    communicator.call();
                } else {
                    communicator.check();
                }
                callButton.setEnabled(false);
                raiseButton.setEnabled(false);
                foldButton.setEnabled(false);
            }
        });

        raiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        foldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.fold();
                callButton.setEnabled(false);
                raiseButton.setEnabled(false);
                foldButton.setEnabled(false);
            }
        });
    }


    void showDialog() {
        FragmentManager manager = getSupportFragmentManager();
        RaiseSliderFragment raiseSliderFragment = new RaiseSliderFragment();
        raiseSliderFragment.show(manager, "dialog");
    }

}