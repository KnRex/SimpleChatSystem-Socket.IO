package com.genie.utils;

import android.util.Log;
import android.widget.Toast;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Karthik on 12/02/16.
 */
public class SocketManager {

    private static SocketManager socketManager;
    Socket mSocket;
    private static final String CHAT_URL = "http://192.168.2.7:8000";

    public synchronized static SocketManager getInstance() {
        if (socketManager == null) {
            socketManager = new SocketManager();
        }
        return socketManager;
    }

    public Socket getSocket(){
        return mSocket;
    }


    public void connect() throws URISyntaxException {
        mSocket = IO.socket(CHAT_URL);
        mSocket.connect();
    }

    public void setOnConnectionError(Emitter.Listener listener) {
        mSocket.on(Socket.EVENT_CONNECT_ERROR, listener);
    }

    public void setOnTimeoutError(Emitter.Listener listener) {
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, listener);
    }

    public void setOnConnection(Emitter.Listener listener) {
        mSocket.on(Socket.EVENT_CONNECT, listener);
    }

    public void setSocketEventListener(String eventName, Emitter.Listener listener){
        mSocket.on(eventName, listener);
    }

    public void disconnect(){
        mSocket.disconnect();
    }

    public void removeOnConnectionError(Emitter.Listener listener){
        mSocket.off(Socket.EVENT_CONNECT_ERROR, listener);
    }

    public void removeOnTimeOutError(Emitter.Listener listener){
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, listener);

    }

    public void removeOnconnection(Emitter.Listener listener){
        mSocket.off(Socket.EVENT_CONNECT, listener);

    }

    public void removeSocketEventListener(String eventName,Emitter.Listener listener){
        mSocket.off(eventName, listener);

    }

}
