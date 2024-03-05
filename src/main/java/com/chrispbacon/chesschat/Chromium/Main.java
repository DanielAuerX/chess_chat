package com.chrispbacon.chesschat.Chromium;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);
		boolean useOSR = arguments.contains("--osr");
		int webserverPort = new Random().nextInt(30000) + 20000;
		Main.bootstrap(arguments);

		try {
			Jamachi.create(webserverPort, useOSR);
		} catch (IOException e) {
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

	private static void bootstrap(List<String> arguments) {
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
			System.err.println(e);
		}
	}

	private static ProcessBuilder getApplicationRestartCommand(List<String> arguments) throws Exception {
		String bin = String.join(File.separator, System.getProperty("java.home"), "bin", "java");
		File self = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		if (!self.getName().endsWith(".jar")) {
			System.err.println("Please manually add the required VM options:");
			for (String requiredVmOption : REQUIRED_VM_OPTIONS) {
				System.err.println(requiredVmOption);
			}
			System.err.println("These options are required to properly run Chromium in OSR mode on Java 9 or higher");
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
