package hu.petrik.bottomnavigation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private TextView info;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.wifi_on:
                    //Android 10 (Api 29) és afelett
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        info.setText("Nincs jogosultság a wifi állapot módosítására");
                        Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                        startActivityForResult(panelIntent, 0);
                    } else {
                        //CHANGE_WIFI_STATE engedély kell, csak Android 10 alatt működik
                        wifiManager.setWifiEnabled(true);
                        info.setText("Wifi bekapcsolva");
                    }
                    break;
                case R.id.wifi_off:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        info.setText("Nincs jogosultság a wifi állapot módosítására");
                        Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                        startActivityForResult(panelIntent, 0);
                    } else {
                        //CHANGE_WIFI_STATE engedély kell, csak Android 10 alatt működik
                        wifiManager.setWifiEnabled(false);
                        info.setText("Wifi kikapcsolva");
                    }
                    break;
                case R.id.wifi_info:
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if(networkInfo.isConnected()){
                        int ipnumber = wifiInfo.getIpAddress();
                        String ipAddress = Formatter.formatIpAddress(ipnumber);
                        info.setText("IP:" + ipAddress);
                    } else {
                        info.setText("Nem csatlakoztál wifi hálózathoz");
                    }
                    break;
            }
            return false;
        });
    }

    private void init() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        info = findViewById(R.id.info);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //ACCESS_WIFI_STATE engedély kell
        wifiInfo = wifiManager.getConnectionInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ||
                    wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING
            ) {
                info.setText("Wifi bekapcsolva");
            } else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED ||
                    wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
                info.setText("Wifi kikapcsolva");
            }
        }
    }
}