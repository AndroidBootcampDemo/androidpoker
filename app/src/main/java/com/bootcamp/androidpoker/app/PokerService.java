package com.bootcamp.androidpoker.app;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bootcamp.androidpoker.app.R;

import java.util.ArrayList;
import java.util.List;

public class PokerService extends Service {
    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;
    private BroadcastReceiver p2pReceiver;
    private IntentFilter intentFilter;

    private List<WifiP2pDevice> peerList;

    public List<WifiP2pDevice> getPeerList() {
        return peerList;
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        PokerService getService() {
            return PokerService.this;
        }
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "service onCreate", Toast.LENGTH_SHORT);
        findAGameServer();
    }

    public void connectToPlayer(String deviceAddress) {
        Toast.makeText(PokerService.this, "Connecting to " + deviceAddress + "...", Toast.LENGTH_SHORT).show();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        p2pManager.connect(p2pChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(PokerService.this, "Connected to peer", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(PokerService.this, "Connection to peer failed", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(PokerService.this, "failed finding peers", Toast.LENGTH_LONG).show();
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
            Toast.makeText(PokerService.this, "onReceive: " + intent.toString(), Toast.LENGTH_LONG).show();
            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (p2pManager != null) {
                    p2pManager.requestPeers(p2pChannel, new PokerPeerListListener());
                }
            }
        }
    }

    public class PokerPeerListListener implements WifiP2pManager.PeerListListener {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            peerList = new ArrayList<WifiP2pDevice>(wifiP2pDeviceList.getDeviceList());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

}