package ro.baltoibogdan.chat.observer;

import socketmessage.SocketMessage;

public interface NetworkServiceSubject {

    public void addObserver(NetworkServiceObserver observer);
    public void removeObserver(NetworkServiceObserver observer);
    public void notifyObservers(SocketMessage socketMessage);

}
