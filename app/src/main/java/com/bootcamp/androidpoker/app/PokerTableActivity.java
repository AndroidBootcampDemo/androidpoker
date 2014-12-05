package com.bootcamp.androidpoker.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Activity that runs on the tablet and shows the poker table.
 */
public class PokerTableActivity extends PokerActivity implements PokerService.MessageListener {

    private static final boolean PLAYING_WITH_BOTS = true;
    private static final TableType TABLE_TYPE = TableType.NO_LIMIT;
    /** The size of the big blind. */
    private static final int BIG_BLIND = 10;
    /** The starting cash per player. */
    private static final int STARTING_CASH = 500;

    class User {

  }

  // maybe User object instead of string
  List<User> usersConnected = new ArrayList<User>();

  Handler mainHandler;

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

    mainHandler = new Handler(getMainLooper());
    runGame();
  }

   public void displayPlayersInfo(final Map<String, Player> players) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    displayPlayersInfo(players);
                }
            });
            return;
        }
        FragmentManager manager = getFragmentManager();
        PlayerListFragment fragment = (PlayerListFragment) manager.findFragmentById(R.id.players);
        PlayerListAdapter playersAdapter = new PlayerListAdapter(PokerTableActivity.this, players);
        fragment.getListView().setAdapter(playersAdapter);
  }


  private void runGame() {
      new AsyncTask<Void, Void, Void>() {

          protected Void doInBackground(Void... args) {
              /** The players at the table. */
              Map<String, Player> players = new HashMap<String, Player>();
              players.put("Player", new Player("Henry",   STARTING_CASH, new BasicBot(0, 75)));
              players.put("Joe",    new Player("Joe",   STARTING_CASH, new BasicBot(0, 75)));
              players.put("Mike",   new Player("Mike",  STARTING_CASH, new BasicBot(25, 50)));
              players.put("Eddie",  new Player("Eddie", STARTING_CASH, new BasicBot(50, 25)));
              UITableObserver tableObserver = new UITableObserver(PokerTableActivity.this);
              displayPlayersInfo(players);

              Table table = new Table(TABLE_TYPE, tableObserver, BIG_BLIND);
              for (Player player : players.values()) {
                  table.addPlayer(player);
              }
              table.run();
              return null;
          }

          protected void onProgressUpdate(Void... progress) {
              // Do nothing.
          }

          protected void onPostExecute(Void result) {
              // Do nothing.
          }
      }.execute();
    }

    /* register the broadcast receiver with the intent values to be matched */
  @Override
  protected void onResume() {
    super.onResume();
    doBindService(new BindingCallback() {
        @Override
        public void onBoundToService() {
            mBoundService.registerMessageListener(PokerTableActivity.this);
            mBoundService.startReceivingMessages();
            Toast.makeText(PokerTableActivity.this, "started receiving messages!", Toast.LENGTH_SHORT).show();
        }
    });
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
    ListView playerList;
    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      playerList = (ListView) inflater.inflate(R.layout.fragment_player_list, container, false);
      return playerList;
    }

    public ListView getListView() {
        return playerList;
    }
  }

    public class UITableObserver implements TableObserver {
        private final String TAG = UITableObserver.class.getSimpleName();

        public UITableObserver(Context context) {

        }

        public void messageReceived(final String message) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    Log.d(TAG, "messageReceived");
                }
            });

        }

        public void joinedTable(TableType type, int bigBlind, List<Player> players) {
            Log.d(TAG, "joinedTable");
        }

        public void handStarted(Player dealer) {
            Log.d(TAG, "handStarted");

        }

        public void actorRotated(Player actor) {
            Log.d(TAG, "actorRotated");

        }

        public void playerUpdated(final Player player) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "playerUpdated");
                }
            });
        }

        public void boardUpdated(final List<Card> cards, final int bet, final int pot) {
            // Remove this!!
            slowDownBots();

            final List<Card> immutableCards = new ArrayList<Card>(cards);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "boardUpdated");

                    ((TextView)findViewById(R.id.bet)).setText("Bet" + " $" + bet);
                    ((TextView)findViewById(R.id.pot)).setText("Pot" + " $" + pot);
                    int noOfCards = (immutableCards == null) ? 0 : immutableCards.size();
                    Log.d("igsolla", "boardUpdated noOfCards: " + noOfCards);
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
                        Card currentCard = immutableCards.get(i);
                        String rank = Card.RANK_SYMBOLS[currentCard.getRank()];
                        char suit = Card.SUIT_SYMBOLS[currentCard.getSuit()];
                        String cardName = "card_" + rank + suit;
                        int id = getResources().getIdentifier(cardName, "drawable", getPackageName());
                        imageView.setImageResource(id);
                    }
                    for (int i = noOfCards; i < 5; i++) {
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
                        int id = getResources().getIdentifier("backcard", "drawable", getPackageName());
                        imageView.setImageResource(id);
                    }
                }
            });
        }

        public void playerActed(Player player) {
            Log.d(TAG, "playerActed");
        }
    }

    public void slowDownBots() {
        if (!PLAYING_WITH_BOTS) {
            return;
        }
        try {
            Thread.sleep(50);
        } catch (Exception e) {

        }
    }
}
