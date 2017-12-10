package com.example.naveed.sharedpref_broadcastreveiver_notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TextWatcher{

    int mNotificationId = 001;
    Button notifyBtn;
    Switch Wifi, Airplane;
    TextView Battery;
    EditText Name;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String mypreference = "mypref";
    public static final String name = "nameKey";
    private WifiManager wifiManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        notifyBtn= (Button) findViewById(R.id.notification_btn);
        Wifi= (Switch) findViewById(R.id.wifi_switch);
        Airplane= (Switch) findViewById(R.id.airplane_switch);
        Battery= (TextView) findViewById(R.id.battery_tv);
        Name= (EditText) findViewById(R.id.name_et);

        this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle("Notification")
                                .setContentText("This is my Notification");
                Notification notification= mBuilder.build();

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, notification);
            }
        });

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        if (sharedpreferences.contains(name)) {
            Name.setText(sharedpreferences.getString(name, ""));
        }
        Name.addTextChangedListener(this);

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        Wifi.setChecked(wifiManager.isWifiEnabled());

        Wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wifiManager.setWifiEnabled(isChecked);
            }

        });

        Airplane.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean state = isAirplaneMode();
                AirplaneMode(state);
            }
        });
    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            Battery.setText("Battery Percentage: "+level+" %");
        }
    };


    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void AirplaneMode(boolean state) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.putInt(this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, state ? 0 : 1);
        } else {
            Settings.System.putInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, state ? 0 : 1);
        }
    }

    public boolean isAirplaneMode() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(this.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
        } else {
            return Settings.System.getInt(this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
        }
    }


    private void managePrefs(){

        String n = Name.getText().toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(name, n);
        editor.commit();
        }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { managePrefs();}

    @Override
    public void afterTextChanged(Editable s) {  }


}
