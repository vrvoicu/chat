package ro.baltoibogdan.chat.observer;

import socketmessage.SocketMessage;

public interface NetworkServiceObserver {

    void onSocketMessage(SocketMessage socketMessage);

}
