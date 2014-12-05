package com.bootcamp.androidpoker.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by freopen on 12/4/14.
 */
public abstract class ClientCommunicator {
    public void call() {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "call");
        } catch (JSONException e) {}
        sendJSON(message);
    }

    public void raise(int amount) {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "raise");
            message.put("amount", amount);
        } catch (JSONException e) {}
        sendJSON(message);
    }

    public void fold() {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "fold");
        } catch (JSONException e) {}
        sendJSON(message);

    }

    public void check() {
        JSONObject message = new JSONObject();
        try {
            message.put("action", "check");
        } catch (JSONException e) {}
        sendJSON(message);

    }

    private void sendJSON(JSONObject message) {

    }
    
    private void parseJSON(JSONObject message) {
        try {
            final int type = message.getInt("message_type");
            final JSONObject args = message.getJSONObject("args");
            if (type == 0) {
                onChangeCash(args.getInt("cash"));
            } else if (type == 1) {
                JSONArray cards = args.getJSONArray("cards");
                onShowCards(cards.getString(0), cards.getString(1));
            } else if (type == 2) {
                OnUpdateBetInfo(args.getInt("min_bet"), args.getInt("current_bet"));
                JSONArray actions = args.getJSONArray("allowed_actions");
                ArrayList<String> list_actions = new ArrayList<String>();
                for (int i = 0; i < actions.length(); ++i) {
                    list_actions.add(actions.getString(i));
                }
                onEnableActions(list_actions);
            }
        } catch (JSONException e) {}
    }

    abstract void onShowCards(String first, String second);

    abstract void onHideCards();

    abstract void onChangeCash(int cash);

    abstract void onEnableActions(List<String> actions);

    abstract void OnUpdateBetInfo(int minBet, int currentBet);
}
