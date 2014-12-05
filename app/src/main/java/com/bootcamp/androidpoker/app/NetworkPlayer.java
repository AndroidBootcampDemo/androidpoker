package com.bootcamp.androidpoker.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Created by freopen on 12/5/14.
 */
public class NetworkPlayer implements Client {

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

        final JSONObject response = receiveJSON();
        try {
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
        } catch (JSONException e) {}
        throw new IllegalStateException();
    }

    private void sendJSON(int message_type, JSONObject args) {
        JSONObject message = new JSONObject();
        try {
            message.put("message_type", message_type);
            message.put("args", args);
        } catch (JSONException e) {}
        sendJSON(message);
    }

    private void sendJSON(JSONObject message) {

    }

    private JSONObject receiveJSON() {
        return null;
    }
}
