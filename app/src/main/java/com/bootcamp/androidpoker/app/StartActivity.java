package com.bootcamp.androidpoker.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 * Activity for creating a meme.
 */
public class StartActivity extends PokerActivity {

    public static final String SERVER_ADDRESS_EXTRA = "server-address";

  private ListView peerListView;
    private PeerListAdapter adapter;

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

      Button findServersButton = (Button) findViewById(R.id.button_find_servers);
      findServersButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              List<WifiP2pDevice> peerList = mBoundService.getPeerList();
              if (peerList == null) {
                  return;
              }
              adapter = new PeerListAdapter(StartActivity.this, peerList);
              peerListView.setAdapter(adapter);
          }
      });

      peerListView = (ListView) findViewById(R.id.peer_list);
      peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              if (i >= adapterView.getCount()) {
                  return;
              }
              final String deviceAddress = ((WifiP2pDevice) adapter.getItem(i)).deviceAddress;
              Intent intent = new Intent(StartActivity.this, PokerHandActivity.class);
              intent.putExtra(SERVER_ADDRESS_EXTRA, deviceAddress);
              startActivity(intent);
              mBoundService.connectToPlayer(deviceAddress, new WifiP2pManager.ActionListener() {
                  @Override
                  public void onSuccess() {
                      Toast.makeText(StartActivity.this, "Connected to peer", Toast.LENGTH_SHORT).show();
                      mBoundService.sendMessage("hello androidpoker", deviceAddress, 8888);
                  }

                  @Override
                  public void onFailure(int reason) {
                      Toast.makeText(StartActivity.this, "Connection to peer failed", Toast.LENGTH_SHORT).show();
                  }
              });
          }
      });

      doBindService(null);
  }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBoundService.stopSelf();
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

}
