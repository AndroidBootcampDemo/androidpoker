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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that runs on the tablet and shows the poker table.
 */
public class PokerTableActivity extends Activity {

  class User {

  }
  private static final int UNKNOWN_WIFI_STATE = -1;
  WifiP2pManager p2pManager;
  Channel p2pChannel;
  BroadcastReceiver p2pReceiver;
  IntentFilter intentFilter;
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

    createWifiGroup();
  }

  /* register the broadcast receiver with the intent values to be matched */
  @Override
  protected void onResume() {
    super.onResume();
    registerReceiver(p2pReceiver, intentFilter);
  }
  /* unregister the broadcast receiver */
  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(p2pReceiver);
  }

  private void createWifiGroup() {
    intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
    //intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    p2pChannel = p2pManager.initialize(this, getMainLooper(), null);
    p2pReceiver = new WiFiDirectBroadcastReceiver(p2pManager, p2pChannel);
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
}
