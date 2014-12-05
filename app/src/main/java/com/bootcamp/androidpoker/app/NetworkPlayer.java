package com.bootcamp.androidpoker.app;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Created by freopen on 12/5/14.
 */
public class NetworkPlayer implements Client {

    public static String TAG = "NetworkPlayer";

    private BluetoothSocket mSocket;

    public  NetworkPlayer(BluetoothSocket socket) {
        mSocket = socket;
    }

    @Override
    public void handStarted(int cash) {
        Log.d("igsolla", "server hand started");

        JSONObject args = new JSONObject();
        try {
            args.put("cash", cash);
        } catch (JSONException e) {}
        sendJSON(0, args);
        Log.d("igsolla", "server hand started return");

    }

    @Override
    public void setCards(List<Card> cards) {
        Log.d("igsolla", "server set cards");

        JSONArray jsonCards = new JSONArray();
        for (int i = 0; i < cards.size(); ++i) {
            jsonCards.put(cards.get(i).toString());
        }
        JSONObject args = new JSONObject();
        try {
            args.put("cards", jsonCards);
        } catch (JSONException e) {}
        sendJSON(1, args);
        Log.d("igsolla", "server set cards return");

    }

    @Override
    public Action act(int minBet, int currentBet, Set<Action> allowedActions) {
        Log.d("igsolla", "server called act on");
        JSONObject args = new JSONObject();
        try {
            args.put("min_bet", minBet);
            args.put("current_bet", currentBet);
            JSONArray jsonActions = new JSONArray();
            for (Action action : allowedActions) {
                jsonActions.put(action.getName());
            }
            args.put("allowed_actions", jsonActions);
        } catch (JSONException e) {}
        sendJSON(2, args);

        Log.d("igsolla", "waiting for response");

        try {
            final JSONObject response = receiveJSON(mSocket);
            final String action = response.getString("action");
            Log.d("igsolla", "response received : " + action);
            if (action.equals("call")) {
                return Action.CALL;
            } else if (action.equals("raise")) {
                return new RaiseAction(response.getInt("amount"));
            } else if (action.equals("fold")) {
                return Action.FOLD;
            } else if (action.equals("check")) {
                return Action.CHECK;
            }
        } catch (Exception e) {
            Log.e(TAG, "failed receiving act", e);
        }

        throw new IllegalStateException();
    }

    private void sendJSON(int message_type, JSONObject args) {
        JSONObject message = new JSONObject();
        try {
            message.put("message_type", message_type);
            message.put("args", args);
            sendJSON(message, mSocket);
        } catch (Exception e) {
            Log.e(TAG, "failed sending message", e);
        }
    }

    public static void sendJSON(JSONObject message, BluetoothSocket socket) throws IOException {
        String string = message.toString() + "\n";

        Log.d(TAG, "sending message: " + string);

        OutputStream out = socket.getOutputStream();

        byte buf[]  = new byte[1024];
        int len;

        InputStream inputStream = new ByteArrayInputStream(
                string.getBytes(StandardCharsets.UTF_8));
        while ((len = inputStream.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        out.flush();
        Log.d(TAG, "message sent");
    }

    public static JSONObject receiveJSON(BluetoothSocket socket) throws IOException, JSONException {
        Log.d("igsolla", "we are here!!!");

        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        Log.d("igsolla", "getting data!!!!");
//        reader.close();

        Log.d(TAG, "received message: " + line);
        return new JSONObject(line);
    }
}
