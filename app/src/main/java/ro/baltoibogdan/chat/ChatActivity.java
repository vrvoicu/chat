package ro.baltoibogdan.chat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ro.baltoibogdan.chat.observer.NetworkServiceObserver;
import ro.baltoibogdan.chat.pojos.MessageInfo;
import socketmessage.SocketMessage;

public class ChatActivity extends AppCompatActivity implements NetworkServiceObserver {

    NetworkService networkService;
    boolean networkServiceBound = false;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> stringArray = new ArrayList<String>();

    private EditText messageEditText;
    private Button sendButton;

    private String to;

    private List<MessageInfo> messagesInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        sendButton = (Button) findViewById(R.id.send_button);

        sendButton.setOnClickListener(sendButtonOnClickListener);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArray){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

//                MessageInfo messageInfo = messagesInfos.get(position);
//
//                if (messageInfo.getToFrom().equals(myself)) {
//                    view.setBackgroundColor(Color.BLUE);
//                } else {
//                    view.setBackgroundColor(Color.CYAN);
//                }

                return view;
            }
        };

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
            sendMessage(id, message);

        }

    };

    private String id;
    private String myself;

    @Override
    protected void onStart() {
        super.onStart();

        Intent startIntent = getIntent();
        myself = startIntent.getStringExtra("myself");
        id = startIntent.getStringExtra("email");

        System.out.println(myself);

        Intent intent = new Intent(this, NetworkService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();

        networkService.removeObserver(ChatActivity.this);

        unbindService(serviceConnection);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
            networkService = (NetworkService) binder.getService();
            networkServiceBound = true;

            networkService.addObserver(ChatActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            networkService.removeObserver(ChatActivity.this);

            networkServiceBound = false;

        }

    };

    public void sendMessage(String to, String message){

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setRequestType(SocketMessage.REQUEST_TYPE_SEND_CHAT_MESSAGE);
        Map<String, String> map = socketMessage.getMap();

        map.put("to", id);
        map.put("message", message);

        stringArray.add(message);
        messagesInfos.add(new MessageInfo(myself, message));

        networkService.sendSocketMessage(socketMessage);

    }

    @Override
    public void onSocketMessage(SocketMessage socketMessage) {

        System.out.println("aaaa");

        if(socketMessage.getResponseType() != SocketMessage.RESPONSE_TYPE_SEND_CHAT_MESSAGE)
            return;

        String message = socketMessage.getMap().get("message");

        stringArray.add(message);
        messagesInfos.add(new MessageInfo(id, message));

        System.out.println("aaaaaa");

        for(MessageInfo messageInfo: messagesInfos)
            System.out.println(messageInfo.getToFrom());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.notifyDataSetChanged();
            }
        });

    }
}
