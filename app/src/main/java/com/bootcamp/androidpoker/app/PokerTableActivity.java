package com.bootcamp.androidpoker.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Activity that runs on the tablet and shows the poker table.
 */
public class PokerTableActivity extends PokerActivity implements PokerService.MessageListener {

  class User {

  }

  // maybe User object instead of string
  List<User> usersConnected = new ArrayList<User>();

  public void setUsersConnected(List<User> users) {
    usersConnected.clear();
    usersConnected.addAll(users);
  }

  public void addUser(User user) {
    usersConnected.add(user);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_poker_table);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    ActivityTableObserver tableObserver = new ActivityTableObserver();
    List<Card> cards = new ArrayList<Card>();
    cards.add(new Card(1, 1));
    cards.add(new Card(2, 2));
    cards.add(new Card(3, 3));
    tableObserver.boardUpdated(cards, 10, 50);

  }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    /* register the broadcast receiver with the intent values to be matched */
  @Override
  protected void onResume() {
    super.onResume();
    doBindService();
    mBoundService.registerMessageListener(this);
    mBoundService.startReceivingMessages();
  }
  /* unregister the broadcast receiver */
  @Override
  protected void onPause() {
    super.onPause();
    mBoundService.stopReceivingMessage();
    doUnbindService();
  }

    @Override
    public void onMessageReceived(String message) {
        Toast.makeText(this, "Received message: " + message, Toast.LENGTH_LONG);
    }

  /**
   * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
   */
  public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
      super();
      this.mManager = manager;
      this.mChannel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        handleWifiStateChanged(context, intent);
      } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        handleWifiPeersChanged(context, intent);
      } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        handleWifiConnectionChanged(context, intent);
      }
      /*else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        handleWifiThisDeviceChanged(context, intent);
      } */
    }

    private void handleWifiStateChanged(Context context, Intent intent) {
      int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
      if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
        Toast.makeText(context, "P2p now enabled", Toast.LENGTH_SHORT).show();
      } else if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
        Toast.makeText(context, "Error: P2p not enabled. Please enable to play.",
            Toast.LENGTH_LONG).show();
      setUsersConnected(new ArrayList<User>());
      } else {
        Toast.makeText(context, "Error: update to wifi p2p did not specify state!",
            Toast.LENGTH_SHORT).show();
      }
    }

    private void handleWifiPeersChanged(Context context, Intent intent) {
//      this.mManager.requestPeers(mChannel, );
      // Call WifiP2pManager.requestPeers() to get a list of current peers
      // update connectd users list
      // display update
    }

    private void handleWifiConnectionChanged(Context context, Intent intent) {
      // Respond to new connection or disconnections
      // not sure yet
    }

    /* public void handleWifiThisDeviceChanged(Context context, Intent intent) {
      // Respond to this device's wifi state changing: not sure this is necessary. may
      // usersList = null
      // update ui with message of need to turn on wifi
    } */
  }

  public static class CardsFragment extends Fragment {
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_cards_on_table, container, false);
    }
  }

  public static class PlayerListFragment extends Fragment {
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_player_list, container, false);
    }
  }

    private class ActivityTableObserver implements TableObserver {
        /** Table type (betting structure). */
        private final TableType TABLE_TYPE = TableType.NO_LIMIT;

        /** The size of the big blind. */
        private static final int BIG_BLIND = 10;

        /** The starting cash per player. */
        private final int STARTING_CASH = 500;

        /** The table. */
        private final Table table;

        /** The players at the table. */
        private final Map<String, Player> players;

        /** The current dealer's name. */
        private String dealerName;

        /** The current actor's name. */
        private String actorName;

        public ActivityTableObserver() {
            table = new Table(TABLE_TYPE, this, BIG_BLIND);
            players = new LinkedHashMap<String, Player>();
        }



        @Override
        public void messageReceived(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        }

        @Override
        public void joinedTable(TableType type, int bigBlind, List<Player> players) {
        }

        @Override
        public void handStarted(Player dealer) {

        }

        @Override
        public void actorRotated(Player actor) {

        }

        @Override
        public void playerUpdated(Player player) {

        }

        @Override
        public void boardUpdated(List<Card> cards, int bet, int pot) {
            ((TextView)findViewById(R.id.bet)).setText("Bet" + " $" + bet);
            ((TextView)findViewById(R.id.pot)).setText("Pot" + " $" + pot);
            int noOfCards = (cards == null) ? 0 : cards.size();
            for (int i = 0; i < noOfCards; i++) {
                ImageView imageView = null;
                if (i == 0)
                    imageView = (ImageView) findViewById(R.id.first_card);
                else if (i == 1)
                    imageView = (ImageView) findViewById(R.id.second_card);
                else if (i == 2)
                    imageView = (ImageView) findViewById(R.id.third_card);
                else if (i == 3)
                    imageView = (ImageView) findViewById(R.id.fourth_card);
                else if (i == 4)
                    imageView = (ImageView) findViewById(R.id.fifth_card);
                Card currentCard = cards.get(i);
                String rank = Card.RANK_SYMBOLS[currentCard.getRank()];
                char suit = Card.SUIT_SYMBOLS[currentCard.getSuit()];
                String cardName = "card_" + rank + suit;
                int id = getResources().getIdentifier(cardName, "drawable", getPackageName());
                imageView.setImageResource(id);
            }
        }

        @Override
        public void playerActed(Player player) {

        }

        @Override
        public Action act(int minBet, int currentBet, Set<Action> allowedActions) {
            return null;
        }
    }
}
