package ro.baltoibogdan.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ro.baltoibogdan.chat.observer.NetworkServiceObserver;
import socketmessage.SocketMessage;

public class FriendsListActivity extends AppCompatActivity implements NetworkServiceObserver {

    private NetworkService networkService;
    private boolean networkServiceBound = false;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> stringArray = new ArrayList<String>();

    private String myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArray);

        ListView listView = (ListView) findViewById(R.id.friends_list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(adaptorOnItemClickListener);
    }

    private AdapterView.OnItemClickListener adaptorOnItemClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // Do something in response to the click

        Intent intent = new Intent(FriendsListActivity.this, ChatActivity.class);
        intent.putExtra("email", stringArray.get(position));
        intent.putExtra("myself", myself);
        startActivity(intent);

        }

    };


    @Override
    protected void onStart() {
        super.onStart();

        Intent startIntent = getIntent();
        myself = startIntent.getStringExtra("myself");

        Intent intent = new Intent(this, NetworkService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();

        networkService.removeObserver(FriendsListActivity.this);

        unbindService(serviceConnection);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
            networkService = (NetworkService) binder.getService();
            networkServiceBound = true;

            networkService.addObserver(FriendsListActivity.this);

            requestFriendsList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            networkService.removeObserver(FriendsListActivity.this);

            networkServiceBound = false;

        }

    };

    private void requestFriendsList(){

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setRequestType(SocketMessage.REQUEST_TYPE_FRIENDS_LIST);

        networkService.sendSocketMessage(socketMessage);

    }

    @Override
    public void onSocketMessage(SocketMessage socketMessage) {

        if(socketMessage.getResponseType() != SocketMessage.RESPONSE_TYPE_FRIENDS_LIST)
            return;

        String list = socketMessage.getMap().get("list");

        String friends [] = list.split(";");

        ((ArrayList)stringArray).clear();

        for(String friend: friends)
            if(friend.length() > 0)
                stringArray.add(friend);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
}
