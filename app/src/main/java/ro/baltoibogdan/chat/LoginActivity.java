package ro.baltoibogdan.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ro.baltoibogdan.chat.observer.NetworkServiceObserver;
import socketmessage.SocketMessage;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements NetworkServiceObserver {

    private TextView messageTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private ProgressBar progressBar;

    NetworkService networkService;
    boolean networkServiceBound = false;

    private String myself = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        messageTextView = (TextView) findViewById(R.id.message);
        emailEditText = (EditText) findViewById(R.id.email);
//        passwordEditText = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.sign_in_button);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        signInButton.setOnClickListener(signInClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, NetworkService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();

        networkService.removeObserver(LoginActivity.this);

        unbindService(serviceConnection);

    }

    private OnClickListener signInClickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            String email = emailEditText.getText().toString();
//            String password = passwordEditText.getText().toString();

            myself = email;

            login(email);

//            progressBar.setVisibility(View.VISIBLE);

//            networkService.login(email, password);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
            networkService = (NetworkService) binder.getService();
            networkServiceBound = true;

            networkService.addObserver(LoginActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            networkService.removeObserver(LoginActivity.this);

            networkServiceBound = false;

        }

    };

    private void login(String email){

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setRequestType(SocketMessage.REQUEST_TYPE_LOGIN);
        socketMessage.getMap().put("email", email);

        networkService.sendSocketMessage(socketMessage);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onSocketMessage(SocketMessage socketMessage) {

        if(socketMessage.getResponseType() != SocketMessage.RESPONSE_TYPE_LOGIN)
            return;

        String result = socketMessage.getMap().get("result");

        if(result == "fail") {

            String message = socketMessage.getMap().get("message");

            messageTextView.setText(message);

            return;
        }

        Intent intent = new Intent(this, FriendsListActivity.class);
        intent.putExtra("myself", myself);
        startActivity(intent);

    }
}

