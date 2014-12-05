package com.bootcamp.androidpoker.app;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igsolla on 12/4/14.
 */
public class PlayerListAdapter extends BaseAdapter {

    private static String SPACE = "    ";

    LayoutInflater layoutInflater;
    Map<String, Player> players;

    public PlayerListAdapter(Context context, Map<String, Player> players) {
        this.layoutInflater = LayoutInflater.from(context);
        this.players = new LinkedHashMap<String, Player>(players);
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        TextView textView = (TextView) layoutInflater.inflate(
                R.layout.peer_list_item, parent, false /* attachToRoot */);
        textView.setTextSize(35);
        textView.setPadding(20, 0, 0, 0);

        List<Player> playerList = new ArrayList<Player>(players.values());
        Action action = playerList.get(i).getAction();
        textView.setText(playerList.get(i).getName() + SPACE
                + "cash:$" + playerList.get(i).getCash() + SPACE
                + "action:" + (action == null ? "None" : action) + SPACE
                + "bet:$" + playerList.get(i).getBet());
        return textView;
    }

    public void updatePlayer(Player player) {
        players.put(player.getName(), player);
        notifyDataSetChanged();
    }
}
