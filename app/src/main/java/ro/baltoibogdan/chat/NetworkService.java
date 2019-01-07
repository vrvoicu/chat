package ro.baltoibogdan.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("onStartCommand");

        try {

            socket = new Socket("localhost", 4321);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
