package com.bootcamp.androidpoker.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PokerService extends Service {

    private MessageReceiverTask receiverTask;
    final public static UUID uuid = new UUID(5465465, 465496846);

    public interface MessageListener {
        public void onMessageReceived(String message);
    }

    public static String TAG = "PokerService";

    private WifiP2pManager p2pManager;
    private WifiP2pManager.Channel p2pChannel;
    private BroadcastReceiver p2pReceiver;
    private IntentFilter intentFilter;

    private List<WifiP2pDevice> peerList;
    private MessageListener messageListener;

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

    public void registerMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "service onCreate", Toast.LENGTH_SHORT);
        findAGameServer();
    }

    public void connectToPlayer(String deviceAddress, WifiP2pManager.ActionListener listener) {
        Toast.makeText(PokerService.this, "Connecting to " + deviceAddress + "...", Toast.LENGTH_SHORT).show();
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        p2pManager.connect(p2pChannel, config, listener);
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

    public void sendMessage(final String message, final String host, final int port) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                int len;
                Socket socket = new Socket();
                byte buf[]  = new byte[1024];
                try {
                    /**
                     * Create a client socket with the host,
                     * port, and timeout information.
                     */
                    socket.bind(null);
                    socket.connect((new InetSocketAddress(host, port)), 500);

                    /**
                     * Create a byte stream from a JPEG file and pipe it to the output stream
                     * of the socket. This data will be retrieved by the server device.
                     */
                    OutputStream outputStream = socket.getOutputStream();
                    ContentResolver cr = context.getContentResolver();
                    InputStream inputStream = new ByteArrayInputStream(
                            message.getBytes(StandardCharsets.UTF_8));
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "could not send message " + e.getMessage(), e);
                }

                /**
                 * Clean up any open sockets when done
                 * transferring or if an exception occurred.
                 */
                finally {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                //catch logic
                            }
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    public void startReceivingMessages() {
        receiverTask = new MessageReceiverTask(this);
        receiverTask.execute();
    }

    public void stopReceivingMessage() {
        if (receiverTask != null) {
            receiverTask.cancel(true);
        }
    }

    public class MessageReceiverTask extends AsyncTask<Void, Void, String> {

        private Context context;

        public MessageReceiverTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();

                InputStream inputstream = client.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
                StringWriter writer = new StringWriter();
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.append(line);
                }
                reader.close();

                return writer.toString();
            } catch (IOException e) {
                Log.e(PokerService.TAG, e.getMessage());
                return null;
            }
        }

        /**
         * Start activity that can handle the JPEG image
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                messageListener.onMessageReceived(result);
            }
            // Start a new listener task.
            receiverTask = new MessageReceiverTask(context);
            receiverTask.execute();
        }
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