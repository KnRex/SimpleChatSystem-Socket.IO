package com.genie.androidchatclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.genie.utils.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private EditText editText;
    private Button sendBtn;
    private ArrayList<String> messageList;
    private ArrayAdapter adapter;
    Socket mSocket;
    private String username;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = (ListView) findViewById(R.id.listview);
        editText = (EditText) findViewById(R.id.editText);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        messageList = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messageList);
        listView.setAdapter(adapter);
        mSocket= SocketManager.getInstance().getSocket();
        username=getIntent().getStringExtra("username");
        userid=getIntent().getStringExtra("userid");
        SocketManager.getInstance().setSocketEventListener("RECEIVE_MESSAGE",onReceiveMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
    public void onClick(View v) {
        if (editText.getText().length() > 0) {
            JSONObject object=new JSONObject();
            try {
                object.put("message",editText.getText().toString());
                object.put("userid",userid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("SENDMESSAGE",object.toString());
            messageList.add(editText.getText().toString());
            editText.getText().clear();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private Emitter.Listener onReceiveMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message=args[0].toString();
                    messageList.add(message);
                    adapter.notifyDataSetChanged();

                }
            });
        }
    };

}
