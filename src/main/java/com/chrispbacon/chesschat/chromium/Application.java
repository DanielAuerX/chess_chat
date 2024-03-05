package com.chrispbacon.chesschat.chromium;

import com.chrispbacon.chesschat.local.LocalExecutor;
import com.chrispbacon.chesschat.remote.RemoteClient;

import java.net.ServerSocket;
import java.util.Optional;

public class Application {
    //private final Debouncer debouncer = new Debouncer();
    //private Optional<RichPresence> richPresence;
    private LocalExecutor localExecutor;
    private RemoteClient remoteClient;
    private SocketServer socketServer;
    private ServerSocket serverSocket;
    private int websocketPort;
    private boolean graceful;
    private String version;
    private String[] args;

    public LocalExecutor getLocalExecutor() {
        return localExecutor;
    }

    public void setLocalExecutor(LocalExecutor localExecutor) {
        this.localExecutor = localExecutor;
    }

    public RemoteClient getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(RemoteClient remoteClient) {
        this.remoteClient = remoteClient;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public int getWebsocketPort() {
        return websocketPort;
    }

    public void setWebsocketPort(int websocketPort) {
        this.websocketPort = websocketPort;
    }

    public boolean isGraceful() {
        return graceful;
    }

    public void setGraceful(boolean graceful) {
        this.graceful = graceful;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public SocketServer getSocketServer() {
        return socketServer;
    }

    public void setSocketServer(SocketServer socketServer) {
        this.socketServer = socketServer;
    }
}
