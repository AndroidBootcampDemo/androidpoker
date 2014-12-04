package com.google.android.bootcamp.poker;

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

/**
 * Created by pschmid on 12/4/14.
 */
public class PeerListAdapter extends BaseAdapter {

  private WifiP2pDeviceList devices;
  private List<WifiP2pDevice> deviceList;
  private final LayoutInflater layoutInflater;

  public PeerListAdapter(Context context, WifiP2pDeviceList devices) {
    this.devices = devices;
    this.layoutInflater = LayoutInflater.from(context);
    this.deviceList = new ArrayList<WifiP2pDevice>(devices.getDeviceList());
  }

  @Override
  public int getCount() {
    return deviceList.size();
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
    textView.setText(deviceList.get(i).toString());
    return textView;
  }
}
