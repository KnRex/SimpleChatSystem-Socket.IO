package com.genie.androidchatclient;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genie.utils.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity  {




    SocketManager mSocketManger;
    Socket mSocket;
    private static final String TAG = "MainActivity";
    private EditText name;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name= (EditText) findViewById(R.id.name);
        btn= (Button) findViewById(R.id.connect);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocketManger=SocketManager.getInstance();
                try {
                    mSocketManger.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                mSocketManger.setOnTimeoutError(onConnectError);
                mSocketManger.setOnConnectionError(onConnectError);
                mSocketManger.setOnConnection(onConnection);

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocketManger.disconnect();
        mSocketManger.removeOnconnection(onConnection);
        mSocketManger.removeOnConnectionError(onConnectError);
        mSocketManger.removeOnTimeOutError(onConnectError);
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


    private Emitter.Listener onConnection = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v(TAG + "==>", "Connected");
            mSocket=mSocketManger.getSocket();
            JSONObject object=new JSONObject();
            try {
                object.put("username",name.getText().toString());
                object.put("userid",getDeviceId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("add user",object.toString());
            MainActivity.this.startActivity(new Intent(MainActivity.this,UsersActivity.class));
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v("Error", args[0].toString());
                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            "Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    };




    public String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext()
                .getSystemService(MainActivity.this.TELEPHONY_SERVICE);

        if (tm.getDeviceId() != null) {

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = ""
                    + android.provider.Settings.Secure.getString(
                    getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            return deviceUuid.toString();
        } else {
            String deviceId = Settings.Secure.getString(getApplicationContext()
                    .getContentResolver(), Settings.Secure.ANDROID_ID);
            return deviceId;
        }

    }



}
