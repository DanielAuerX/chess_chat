package com.chrispbacon.chesschat.local;

import com.chrispbacon.chesschat.chromium.Application;
import com.chrispbacon.chesschat.local.util.DownloadCallback;
import com.chrispbacon.chesschat.local.util.HostType;
import com.chrispbacon.chesschat.local.util.Network;
import com.chrispbacon.chesschat.remote.RemoteClient;
import io.javalin.http.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class LocalExecutor implements DownloadCallback {
    private final Application application;
    private HostType hostType = HostType.UNKNOWN;
    private double length, value = -1D;
    private JSONObject release;
    private String chatId;
    private int current;

    public LocalExecutor(Application application) {
        this.application = application;
    }

    public HostType getHostType() {
        return hostType;
    }

    public String getChatId() {
        return chatId;
    }

    public void configure() {
        path("/v1", () -> {
            path("/config", () -> {
                get("/websocket", context -> context.result(String.valueOf(application.getWebsocketPort())));
            });
        });
    }

    private void reset(Application application) {
        this.chatId = null;
    }

    private BiConsumer<Context, Application> OPEN = (context, application) -> {
        String url = context.pathParam("url");
        String plain = new String(Base64.getDecoder().decode(url.getBytes()));
        if (!plain.startsWith("https")) return;
        try {
            Network.browse(plain);
        } catch (IOException e) {
            System.out.println(e);
        }
    };

    private BiConsumer<Context, Application> HOST = (context, application) -> {
        reset(application);
        this.hostType = HostType.HOST;
        RemoteClient remoteClient = application.getRemoteClient();
        JSONObject object = remoteClient.executeBlocking("create");
        this.chatId = object.getString("result").split(" ")[0];
        context.result(object.toString());
    };

    private BiConsumer<Context, Application> MESSAGE = (context, application) -> {
        RemoteClient remoteClient = application.getRemoteClient();
        String message = context.pathParam("message");
        JSONObject response = remoteClient.executeBlocking(
                "chat",
                chatId,
                message
        );
    };


    private void write(Path path, byte[] b) throws IOException {
        Files.write(path, b, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    //private byte[] read(HttpURLConnection connection, DownloadCallback callback) throws IOException {
    //    try (InputStream stream = connection.getInputStream()) {
    //        return BasicHttp.get(stream, callback);
    //    } catch (IOException e1) {
    //        try (InputStream stream = connection.getErrorStream()) {
    //            return stream == null ? null : BasicHttp.get(stream, callback);
    //        } catch (IOException e2) {
    //            throw e2;
    //        }
    //    }
    //}

    @Override
    public void add(int i) {
        this.current += i;
        if (length == -1) return;
        JSONObject object = new JSONObject();
        object.put("instruction", "download");
        double current = Math.floor((this.current / length) * 100D);
        if (current != value) {
            value = current;
            object.put("progress", value);
            application.getSocketServer().forward(object.toString());
        }
    }

    @Override
    public void notify(int i) {
        this.length = i;
    }
}