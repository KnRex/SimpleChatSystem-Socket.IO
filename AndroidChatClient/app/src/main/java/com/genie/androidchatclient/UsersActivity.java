package com.genie.androidchatclient;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.genie.utils.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class UsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView usersList;
    ArrayAdapter adapter;
    ArrayList<String> list;
    ArrayList<String> userIdList;
    Socket mSocket;
    SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        usersList = (ListView) findViewById(R.id.usersList);
        list = new ArrayList<>();
        userIdList=new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        usersList.setAdapter(adapter);
        socketManager = SocketManager.getInstance();
        socketManager.setSocketEventListener("GET_USER_LIST", onUserListListener);
        mSocket = socketManager.getSocket();
        mSocket.emit("GET_USERS", "userslist");
        usersList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_users, menu);
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


    private Emitter.Listener onUserListListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            UsersActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String deviceID = UsersActivity.this.getDeviceId();

                    JSONArray array = (JSONArray) args[0];
                    Log.v("value", array.length() + "");


                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject object = (JSONObject) array.get(i);
                            if (deviceID.equals(object.getString("userid"))) {

                            } else{
                                list.add(object.getString("username"));
                                userIdList.add(object.getString("userid"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
            });
        }
    };

    public String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext()
                .getSystemService(UsersActivity.this.TELEPHONY_SERVICE);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.removeSocketEventListener("GET_USER_LIST", onUserListListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent =new Intent(this,ChatActivity.class);
        intent.putExtra("username",list.get(position));
        intent.putExtra("userid",userIdList.get(position));
        startActivity(intent);

    }


}
