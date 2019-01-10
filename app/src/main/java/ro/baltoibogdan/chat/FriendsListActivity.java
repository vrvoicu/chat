package ro.baltoibogdan.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendsListActivity extends AppCompatActivity {

    private ArrayAdapter<String> arrayAdapter;
    private String[] stringArray = new String[]{"ASD", "BSD", "CSD"};

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
            intent.putExtra("email", stringArray[position]);
            startActivity(intent);
        }

    };

}
