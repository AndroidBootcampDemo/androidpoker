package com.bootcamp.androidpoker.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity for creating a meme.
 */
public class StartActivity extends Activity {

  private WifiP2pManager p2pManager;
  private Channel p2pChannel;
  private BroadcastReceiver p2pReceiver;
  private IntentFilter intentFilter;
  private ListView peerListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    Button createGameButton = (Button) findViewById(R.id.button_create_game);
    createGameButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(StartActivity.this, PokerTableActivity.class));
      }
    });

    Button joinGameButton = (Button) findViewById(R.id.button_join_game);
    joinGameButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO
      }
    });

    peerListView = (ListView) findViewById(R.id.peer_list);
  }

  /* register the broadcast receiver with the intent values to be matched */
  @Override
  protected void onResume() {
    super.onResume();
    findAGameServer();
  }
  /* unregister the broadcast receiver */
  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(p2pReceiver);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void findAGameServer() {
    p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    p2pChannel = p2pManager.initialize(this, getMainLooper(), null);
    p2pReceiver = new WiFiDirectBroadcastReceiver();

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
    registerReceiver(p2pReceiver, intentFilter);

    p2pManager.discoverPeers(p2pChannel, new WifiP2pManager.ActionListener() {
      @Override
      public void onSuccess() {
      }

      @Override
      public void onFailure(int reasonCode) {
        Toast.makeText(StartActivity.this, "failed finding peers", Toast.LENGTH_LONG).show();
      }
    });
  }

  /**
   * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
   */
  public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    public WiFiDirectBroadcastReceiver() {
      super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      Toast.makeText(StartActivity.this, "onReceive: " + intent.toString(), Toast.LENGTH_LONG).show();
      if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        if (p2pManager != null) {
          p2pManager.requestPeers(p2pChannel, new PokerPeerListListener());
        }
      }
    }
  }

  public class PokerPeerListListener implements PeerListListener {

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
      peerListView.setAdapter(new PeerListAdapter(StartActivity.this, wifiP2pDeviceList));
      Toast.makeText(StartActivity.this, wifiP2pDeviceList.getDeviceList().size() + " devices found", Toast.LENGTH_LONG);
    }
  }

}
