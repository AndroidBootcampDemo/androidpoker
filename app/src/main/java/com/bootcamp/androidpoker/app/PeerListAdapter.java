package com.bootcamp.androidpoker.app;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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

  private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
  private final LayoutInflater layoutInflater;

  public PeerListAdapter(Context context) {
    this.layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    return deviceList.size();
  }

  @Override
  public Object getItem(int i) {
    return deviceList.get(i);
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

    public void addDevice(BluetoothDevice device) {
        deviceList.add(device);
        notifyDataSetChanged();
    }
}
