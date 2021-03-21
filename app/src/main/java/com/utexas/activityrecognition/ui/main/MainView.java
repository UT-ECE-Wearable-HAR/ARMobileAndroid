package com.utexas.activityrecognition.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.api.impl.RecogitionAPIImpl;
import com.utexas.activityrecognition.ui.bluetooth.connect.BluetoothServiceKt;
import com.utexas.activityrecognition.ui.bluetooth.connect.MyBluetoothService;
import com.utexas.activityrecognition.ui.tcp.TcpClient;

public class MainView extends AppCompatActivity {
    private static final String TAG = MainView.class.getCanonicalName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(BluetoothServiceKt.isRunning()) {
            menu.getItem(1).setEnabled(true);
            menu.getItem(0).setEnabled(false);
        } else {
            menu.getItem(1).setEnabled(false);
            menu.getItem(0).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.three_dot_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_connect:
                MyBluetoothService.Companion.setDebugHandler(new DebugHandler());
                RecogitionAPIImpl.getInstance().ConnectImgSocket(this);
                Intent connectWearable = new Intent(this, MyBluetoothService.class);
                startForegroundService(connectWearable);
                return true;
            case R.id.menu_disconnect:
                Intent disconnectWearable = new Intent(this, MyBluetoothService.class);
                stopService(disconnectWearable);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class DebugHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            TextView debugText = (TextView) (MainView.this.findViewById(R.id.debugText));
            debugText.setText((String) msg.obj);
        }
    }
}