package ro.baltoibogdan.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ro.baltoibogdan.chat.observer.NetworkServiceObserver;
import ro.baltoibogdan.chat.observer.NetworkServiceSubject;
import socketmessage.SocketMessage;

public class NetworkService extends Service implements NetworkServiceSubject {

    private LocalBinder binder = new LocalBinder();

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public NetworkService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

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

                        SocketMessage socketMessage = (SocketMessage) obj;

                        notifyObservers(socketMessage);

                    } catch (ClassNotFoundException e) {

                        System.out.println("aaa");

                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();

                        System.out.println("bbb");

                        isConnected = false;
                        run = false;
                    }
                }

            }

        };

        communicationThread = new Thread(communicationThread);
        ((Thread)communicationThread).start();
    }

    private boolean isConnected = false;

    private synchronized void connect(){

        if(isConnected)
            return;

        System.out.println(this);

        try {

            System.out.println("wtf");

            socket = new Socket("192.0.0.122", 4322);
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

    public void sendSocketMessage(SocketMessage socketMessage){

        connect();

        try {
            oos.writeObject(socketMessage);
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
        }

    }

    private List<NetworkServiceObserver> observers = new ArrayList<NetworkServiceObserver>();

    @Override
    public void addObserver(NetworkServiceObserver observer) {

        if(!observers.contains(observer))
            observers.add(observer);

    }

    @Override
    public void removeObserver(NetworkServiceObserver observer) {

        if(observers.contains(observer))
            observers.remove(observer);

    }


    @Override
    public void notifyObservers(SocketMessage socketMessage) {

        for(NetworkServiceObserver observer: observers)
            observer.onSocketMessage(socketMessage);

    }
}
