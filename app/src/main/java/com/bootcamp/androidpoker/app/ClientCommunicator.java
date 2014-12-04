package com.bootcamp.androidpoker.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by freopen on 12/4/14.
 */
public abstract class ClientCommunicator {
    public void call() {
        JSONObject response = new JSONObject();
        try {
            response.put("action", "call");
        } catch (JSONException e) {}
        sendJSON(response);
    }

    public void raise(int amount) {
        JSONObject response = new JSONObject();
        try {
            response.put("action", "raise");
            response.put("amount", amount);
        } catch (JSONException e) {}
        sendJSON(response);
    }

    public void fold() {
        JSONObject response = new JSONObject();
        try {
            response.put("action", "fold");
        } catch (JSONException e) {}
        sendJSON(response);

    }

    public void check() {
        JSONObject response = new JSONObject();
        try {
            response.put("action", "check");
        } catch (JSONException e) {}
        sendJSON(response);

    }

    private void sendJSON(JSONObject response) {

    }

    abstract void onShowCards(String first, String second);

    abstract void onHideCards();

    abstract void onChangeCash(int cash);

    abstract void onEnableActions(List<String> actions);

    abstract void OnUpdateBetInfo(int minBet, int currentBet);
}
