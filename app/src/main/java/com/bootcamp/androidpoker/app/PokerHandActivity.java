package com.bootcamp.androidpoker.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    public int currentCash = 0;
    public int minBet;
    public int currentBet;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poker_hand);
Log.d("igsolla", "PokerHandActivity onCreate");

        doBindService(new BindingCallback() {
            @Override
            public void onBoundToService() {
                onBound();
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    void onBound() {
        communicator = new ClientCommunicator(mBoundService.clientSocket) {
            @Override
            void onShowCards(String first, String second) {
                vibrate();
                ImageView handFirst = (ImageView) findViewById(R.id.hand_first);
                int firstId = getResources().getIdentifier("card_" + first, "drawable", getPackageName());
                handFirst.setImageResource(firstId);

                ImageView handSecond = (ImageView) findViewById(R.id.hand_second);
                int secondId = getResources().getIdentifier("card_" + second, "drawable", getPackageName());
                handSecond.setImageResource(secondId);
                communicator.listenForInput();
            }

            @Override
            void onHideCards() {
                vibrate();
                ImageView handFirst = (ImageView) findViewById(R.id.hand_first);
                handFirst.setImageResource(R.drawable.card_blue_back);

                ImageView handSecond = (ImageView) findViewById(R.id.hand_second);
                handSecond.setImageResource(R.drawable.card_blue_back);
                communicator.listenForInput();
            }

            @Override
            void onChangeCash(int cash) {
                vibrate();
                if (cash > currentCash) {
                    Toast.makeText(PokerHandActivity.this, "You got money!", Toast.LENGTH_LONG).show();
                }
                currentCash = cash;
                TextView cashValue = (TextView) findViewById(R.id.cash_value);
                cashValue.setText("$" + currentCash);
                communicator.listenForInput();
            }

            @Override
            void onEnableActions(List<String> actions) {
                vibrate();
                final Button callButton = (Button) findViewById(R.id.call_button);
                final Button raiseButton = (Button) findViewById(R.id.raise_button);
                final Button foldButton = (Button) findViewById(R.id.fold_button);
                callButton.setEnabled(false);
                raiseButton.setEnabled(false);
                foldButton.setEnabled(false);

                if (actions.contains("Call")) {
                    callButton.setEnabled(true);
                    callButton.setText("Call");
                }
                if (actions.contains("Check")) {
                    callButton.setEnabled(true);
                    callButton.setText("Check");
                }
                if (actions.contains("Raise")) {
                    raiseButton.setEnabled(true);
                }
                if (actions.contains("Fold")) {
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
                if (callButton.getText().equals("Call")) {
                    communicator.call();
                } else {
                    communicator.check();
                }
                callButton.setEnabled(false);
                raiseButton.setEnabled(false);
                foldButton.setEnabled(false);

                communicator.listenForInput();
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
                communicator.listenForInput();
            }
        });

        communicator.listenForInput();
    }

    void vibrate() {
        vibrator.vibrate(100);
    }


    void showDialog() {
        FragmentManager manager = getSupportFragmentManager();
        RaiseSliderFragment raiseSliderFragment = new RaiseSliderFragment();
        raiseSliderFragment.show(manager, "dialog");
    }

}