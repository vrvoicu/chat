package ro.baltoibogdan.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import socketmessage.SocketMessage;

public class NetworkService extends Service {

    private LocalBinder binder = new LocalBinder();

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public NetworkService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("onCreate");

    }

    private boolean run = true;

    private Runnable communicationThread;

    private void startCommunicationThread(){
        communicationThread = new Runnable(){

            @Override
            public void run() {

                while(run){

                    try {

                        Object obj = ois.readObject();

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();

                        isConnected = false;
                    }
                }

            }

        };
    }

    private boolean isConnected = false;

    private void connect(){

        if(isConnected)
            return;

        try {

            socket = new Socket("192.0.0.122", 4321);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            isConnected = true;

            startCommunicationThread();

        } catch (IOException e) {
            e.printStackTrace();

            try {
                if(ois != null)
                    ois.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                if(oos != null)
                    oos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("onStartCommand");

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");

        return binder;
    }

    class LocalBinder extends Binder {

        Service getService(){
            return NetworkService.this;
        }
    }

    public void login(String email, String password){

//        connect();

//        try {
            System.out.println("login");
//            oos.writeObject(email);
       /* } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public void sendMessage(String to, String message){

        connect();

        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setRequestType(SocketMessage.REQUEST_TYPE_SEND_CHAT_MESSAGE);
        Map<String, String> map = socketMessage.getMap();

        map.put("to", to);
        map.put("message", message);

        sendSocketMessage(socketMessage);

    }

    private void sendSocketMessage(SocketMessage socketMessage){

        try {
            oos.writeObject(socketMessage);
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
        }

    }
}
