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
import java.util.List;
import java.util.Map;

/**
 * Created by igsolla on 12/4/14.
 */
public class PlayerListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Player> players;

    public PlayerListAdapter(Context context, Map<String, Player> players) {
        this.layoutInflater = LayoutInflater.from(context);
        this.players = new ArrayList<Player>(players.values());
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
        textView.setText(players.get(i).getName());
        return textView;
    }
}
