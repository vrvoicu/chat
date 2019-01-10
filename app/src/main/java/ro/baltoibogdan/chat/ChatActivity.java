package ro.baltoibogdan.chat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends AppCompatActivity {

    NetworkService networkService;
    boolean networkServiceBound = false;

    private ArrayAdapter<String> arrayAdapter;
    private String[] stringArray = new String[]{"hello", "suck it", "you suck it"};

    private EditText messageEditText;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        sendButton = (Button) findViewById(R.id.send_button);

        sendButton.setOnClickListener(sendButtonOnClickListener);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArray);

        ListView listView = (ListView) findViewById(R.id.messages_list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(adaptorOnItemClickListener);
    }

    private AdapterView.OnItemClickListener adaptorOnItemClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // Do something in response to the click

//            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
//            intent.putExtra("email", stringArray[position]);
//            startActivity(intent);
        }

    };

    private View.OnClickListener sendButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String message = messageEditText.getText().toString();

            networkService.sendMessage("test", message);
        }

    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, NetworkService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
            networkService = (NetworkService) binder.getService();
            networkServiceBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            networkServiceBound = false;

        }

    };
}
