package com.example.utmpapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.example.utmpapp.Network.HostBean;
import com.example.utmpapp.Network.NetInfo;
import com.example.utmpapp.Utils.Prefs;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

public class AllGeofencesActivity extends ActivityNet {

  // region Overrides
  private final String TAG = "AllGeofencesDiscovery";
  public final static long VIBRATE = (long) 250;
  public final static int SCAN_PORT_RESULT = 1;
  public static final int MENU_SCAN_SINGLE = 0;
  public static final int MENU_OPTIONS = 1;
  public static final int MENU_HELP = 2;
  private static final int MENU_EXPORT = 3;
  private static LayoutInflater mInflater;
  private int currentNetwork = 0;
  private long network_ip = 0;
  private long network_start = 0;
  private long network_end = 0;
  private List<HostBean> hosts = null;
  /*  private HostsAdapter adapter;*/
  ActionButton actionButton;
  private AbstractDiscovery mDiscoveryTask = null;

  SharedPreferences SharedPrefs;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_all_geofences);

    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
              .add(R.id.container, new AllGeofencesFragment())
              .commit();
    }

    GeofenceController.getInstance().init(this);
    //SharedPrefs = getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE);
    //Toast.makeText(this,SharedPrefs.getString("emailKey","opq@gmail.com"),Toast.LENGTH_LONG).show();
    // Discover
/*    actionButton = (ActionButton) findViewById(R.id.fragment_all_geofences_actionButton);
    actionButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startDiscovering();
      }
    });*/
  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_all_geofences, menu);

    MenuItem item = menu.findItem(R.id.action_delete_all);

    if (GeofenceController.getInstance().getNamedGeofences().size() == 0) {
      item.setVisible(false);
    }

    return true;
  }

  @Override
  public void onResume() {
    super.onResume();

    int googlePlayServicesCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    Log.i(AllGeofencesActivity.class.getSimpleName(), "googlePlayServicesCode = " + googlePlayServicesCode);

    if (googlePlayServicesCode == 1 || googlePlayServicesCode == 2 || googlePlayServicesCode == 3) {
      GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCode, this, 0).show();
    }
  }

  @Override
  public void onBackPressed() {
    if (this.isFinishing()){
      finish();
      super.onBackPressed();
    }
  }
  @Override
  protected void setInfo() {

    if (currentNetwork != net.hashCode()) {
      Log.i(TAG, "Network info has changed");
      currentNetwork = net.hashCode();

      // Cancel running tasks
      cancelTasks();
    } else {
      return;
    }

    // Get ip information
    network_ip = NetInfo.getUnsignedLongFromIp(net.ip);
    if (prefs.getBoolean(Prefs.KEY_IP_CUSTOM, Prefs.DEFAULT_IP_CUSTOM)) {
      // Custom IP
      network_start = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_START,
              Prefs.DEFAULT_IP_START));
      network_end = NetInfo.getUnsignedLongFromIp(prefs.getString(Prefs.KEY_IP_END,
              Prefs.DEFAULT_IP_END));
    } else {
      // Custom CIDR
      if (prefs.getBoolean(Prefs.KEY_CIDR_CUSTOM, Prefs.DEFAULT_CIDR_CUSTOM)) {
        net.cidr = Integer.parseInt(prefs.getString(Prefs.KEY_CIDR, Prefs.DEFAULT_CIDR));
      }
      // Detected IP
      int shift = (32 - net.cidr);
      if (net.cidr < 31) {
        network_start = (network_ip >> shift << shift) + 1;
        network_end = (network_start | ((1 << shift) - 1)) - 1;
      } else {
        network_start = (network_ip >> shift << shift);
        network_end = (network_start | ((1 << shift) - 1));
      }
      // Reset ip start-end (is it really convenient ?)
      SharedPreferences.Editor edit = prefs.edit();
      edit.putString(Prefs.KEY_IP_START, NetInfo.getIpFromLongUnsigned(network_start));
      edit.putString(Prefs.KEY_IP_END, NetInfo.getIpFromLongUnsigned(network_end));
      edit.commit();
    }
  }
  @Override
  protected void setButtons(boolean disable) {

  }

  @Override
  protected void cancelTasks() {

  }
  public void startDiscovering() {
    mDiscoveryTask = new DefaultDiscovery(AllGeofencesActivity.this);
    mDiscoveryTask.setNetwork(network_ip, network_start, network_end);
    mDiscoveryTask.execute();
  }

  public void stopDiscovering() {
    Log.e(TAG, "stopDiscovering()");
    mDiscoveryTask = null;
  }
  private void initList() {
    hosts = new ArrayList<HostBean>();
  }

  public void addHost(HostBean host) {
    host.position = hosts.size();
    hosts.add(host);
  }
  // endregion
}
