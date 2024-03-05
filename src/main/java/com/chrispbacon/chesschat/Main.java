package com.chrispbacon.chesschat;


import com.chrispbacon.chesschat.chromium.Application;
import com.chrispbacon.chesschat.chromium.Jamachi;
import com.chrispbacon.chesschat.chromium.SocketServer;
import com.chrispbacon.chesschat.local.LocalExecutor;
import com.chrispbacon.chesschat.local.util.ExecutorManager;
import com.chrispbacon.chesschat.remote.RemoteClient;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static final int SELF_PORT = 42069;
	private static boolean failed;

	public static void main(String[] args) {
		Application application = new Application();
		ExecutorManager.registerService("pool", Executors.newCachedThreadPool());
		List<String> arguments = Arrays.asList(args);
		boolean useOSR = arguments.contains("--osr");
		application.setArgs(args);
		Main.bootstrap(application, arguments, useOSR);
		try{
			RemoteClient remoteClient = RemoteClient.createAndConnectClientInstance("daniel");
			application.setRemoteClient(remoteClient);
			int websocketPort = new Random().nextInt(30000) + 20000;
			application.setWebsocketPort(websocketPort);
			int webserverPort = new Random().nextInt(30000) + 20000;
			LocalExecutor localExecutor = new LocalExecutor(application);
			application.setLocalExecutor(localExecutor);
			Javalin.create(config -> config.addStaticFiles("/html", Location.CLASSPATH))
					.before("/v1/*", context -> {
						context.header("Access-Control-Allow-Origin", "*");
					})
					.routes(localExecutor::configure)
					.start(webserverPort);

			SocketServer socketServer = SocketServer.launch(websocketPort);
			application.setSocketServer(socketServer);

			if (Main.failed) Thread.sleep(3000L);
			Jamachi.create(webserverPort, useOSR);

		}catch (IOException | InterruptedException e){
			System.out.println(e);
		}

	}

	private static final String[] REQUIRED_VM_OPTIONS = new String[]{
			"--add-exports=java.desktop/sun.java2d=ALL-UNNAMED",
			"--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
			"--add-exports=java.base/java.lang=ALL-UNNAMED"
	};

	private static boolean validateExportOptions() {
		String version = System.getProperty("java.version");
		int major = Integer.parseInt(version.split("\\.")[0]);
		if (major <= 15) return true;
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		for (String option : REQUIRED_VM_OPTIONS) {
			if (!arguments.contains(option)) return false;
		}
		return true;
	}

	private static void bootstrap(Application application, List<String> arguments, boolean useOSR) {
		singletonInstance(application);
		if (useOSR && !validateExportOptions()) {
			try {
				ProcessBuilder builder = getApplicationRestartCommand(arguments);
				System.out.println("Restarting with required VM Options");
				Process process = builder.start();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = in.readLine()) != null) {
						System.out.println(line);
					}
				}
				System.exit(1);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private static void singletonInstance(Application application) {
		System.out.println("AAAAAAAAAAAA");
		ExecutorService service = ExecutorManager.getService("pool");
		service.execute(() -> {
			int attempts = 0;
			do {
				if (application.isGraceful()) break;
				try {
					ServerSocket socket = new ServerSocket(SELF_PORT);
					application.setServerSocket(socket);
					socket.accept();
				} catch (IOException e) {
					if (!application.isGraceful()) System.out.println(e);;
				}
				Main.failed = true;
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			} while (++attempts < 3);
			System.out.println("SingletonInstance down");
			if (!application.isGraceful()) System.exit(1);
		});
	}

	private static ProcessBuilder getApplicationRestartCommand(List<String> arguments) throws Exception {
		String bin = String.join(File.separator, System.getProperty("java.home"), "bin", "java");
		File self = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		if (!self.getName().endsWith(".jar")) {
			System.err.println("Please manually add the required VM options:");
			for (String requiredVmOption : REQUIRED_VM_OPTIONS) {
				System.err.println(requiredVmOption);
			}
			System.err.println("These options are required to properly run chromium in OSR mode on Java 9 or higher");
			throw new Exception("Please add the required VM Options or downgrade your Java version");
		}
		ArrayList<String> command = new ArrayList<>();
		command.add(bin);
		command.addAll(Arrays.asList(REQUIRED_VM_OPTIONS));
		command.add("-jar");
		command.add(self.getPath());
		command.addAll(arguments);
		return new ProcessBuilder(command);
	}
}
