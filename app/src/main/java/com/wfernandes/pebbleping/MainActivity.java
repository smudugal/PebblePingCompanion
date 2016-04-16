package com.wfernandes.pebbleping;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID APP_UUID = UUID.fromString("538ead34-63d4-40d5-8b7f-ee07c9eddf3d");
    private PebbleKit.PebbleDataReceiver mDataReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        StringBuilder builder = new StringBuilder();
        builder.append("Pebble Info\n\n");

        boolean isConnected = PebbleKit.isWatchConnected(this);
        builder.append("Watch Connected: " + (isConnected ? "true" : "false")).append("\n");

        PebbleKit.FirmwareVersionInfo info = PebbleKit.getWatchFWVersion(MainActivity.this);
        builder.append("Firmware Version: ");
        builder.append(info.getMajor()).append(".").append(info.getMinor()).append("\n");

        boolean appMessageSupported = PebbleKit.areAppMessagesSupported(MainActivity.this);
        builder.append("AppMessage supported: " + (appMessageSupported ? "true" : "false") + "\n");

        TextView textView = (TextView)findViewById(R.id.isConnected);
        textView.setText(builder.toString());

        if(mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                    // Message Received
                    PebbleKit.sendAckToPebble(context, transactionId);
                    Log.i("receivedData", "Got message from Pebble");
                    Uri mNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), mNotification);
                    ringtone.play();
                }
            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }


    }
}
