package com.chrispbacon.chesschat.chromium;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SocketServer extends WebSocketServer {
    private final List<String> cache = new LinkedList<>();
    private final List<String> queue = new ArrayList<>();
    private boolean connected;

    public SocketServer(InetSocketAddress address) {
        super(address);
    }

    /**
     * launches a WebSocket on the specified port that allows connections and communication
     *
     * @param websocketPort the port to bind
     */
    public static SocketServer launch(int websocketPort) {
        SocketServer instance = new SocketServer(new InetSocketAddress(websocketPort));
        instance.start();
        return instance;
    }

    /**
     * sends a message to all connected Clients
     *
     * @param message the message to forward
     */
    public void forward(String message) {
        this.cache.add(message);
        if (connected) {
            this.broadcast(message);
        } else {
            this.queue.add(message);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Chromium connected to local WebSocket server");
        if (this.connected = true) {
            for (String message : queue) {
                forward(message);
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Chromium disconnected from local WebSocket server");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        System.out.println(e);
    }

    @Override
    public void onStart() {
        System.out.println("Started WebSocket server");
    }
}
