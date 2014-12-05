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
        JSONObject args = new JSONObject();
        try {
            args.put("cash", cash);
        } catch (JSONException e) {}
        sendJSON(0, args);
    }

    @Override
    public void setCards(List<Card> cards) {
        JSONArray jsonCards = new JSONArray();
        for (int i = 0; i < cards.size(); ++i) {
            jsonCards.put(cards.get(i).toString());
        }
        JSONObject args = new JSONObject();
        try {
            args.put("cards", jsonCards);
        } catch (JSONException e) {}
        sendJSON(1, args);
    }

    @Override
    public Action act(int minBet, int currentBet, Set<Action> allowedActions) {
        JSONObject args = new JSONObject();
        try {
            args.put("min_bet", minBet);
            args.put("current_bet", currentBet);
            JSONArray jsonActions = new JSONArray();
            for (Action action : allowedActions) {
                jsonActions.put(action.getName());
            }
        } catch (JSONException e) {}
        sendJSON(2, args);

        try {
            final JSONObject response = receiveJSON(mSocket);
            final String action = response.getString("action");
            if (action == "call") {
                return new CallAction();
            } else if (action == "raise") {
                return new RaiseAction(response.getInt("amount"));
            } else if (action == "fold") {
                return new FoldAction();
            } else if (action == "check") {
                return new CheckAction();
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
        String string = message.toString();
        Log.d(TAG, "sending message: " + string);

        OutputStream out = socket.getOutputStream();

        byte buf[]  = new byte[1024];
        int len;

        InputStream inputStream = new ByteArrayInputStream(
                string.getBytes(StandardCharsets.UTF_8));
        while ((len = inputStream.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        Log.d(TAG, "message sent");
    }

    public static JSONObject receiveJSON(BluetoothSocket socket) throws IOException, JSONException {
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringWriter writer = new StringWriter();
        String line;
        while ((line = reader.readLine()) != null) {
            writer.append(line);
        }
        reader.close();
        String message = reader.toString();
        Log.d(TAG, "received message: " + message);
        return new JSONObject(message);
    }
}
