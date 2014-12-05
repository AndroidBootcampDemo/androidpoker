package com.bootcamp.androidpoker.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Activity for creating a meme.
 */
public class StartActivity extends PokerActivity {

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

      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
      BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
              if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                  BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                  adapter.addDevice(device);
              }
          }
      };
      registerReceiver(receiver, intentFilter);

    Button joinGameButton = (Button) findViewById(R.id.button_join_game);
    joinGameButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        BluetoothAdapter.getDefaultAdapter().startDiscovery();
      }
    });

      peerListView = (ListView) findViewById(R.id.peer_list);
      adapter = new PeerListAdapter(this);
      peerListView.setAdapter(adapter);
      peerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              if (i >= adapterView.getCount()) {
                  return;
              }
              final BluetoothDevice device = ((BluetoothDevice) adapter.getItem(i));
              new BluetoothClientConnectTask(device).execute();
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

    public class BluetoothClientConnectTask extends AsyncTask<Void, Void, BluetoothSocket> {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        public BluetoothClientConnectTask(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(PokerService.uuid);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        @Override
        protected BluetoothSocket doInBackground(Void... params) {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return null;
            }
            return mmSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (mmSocket != null) {
                Toast.makeText(StartActivity.this, "have socket!", Toast.LENGTH_SHORT).show();
                mBoundService.clientSocket = mmSocket;
                Intent intent = new Intent(StartActivity.this, PokerHandActivity.class);
                startActivity(intent);
            }
        }
    }

}
