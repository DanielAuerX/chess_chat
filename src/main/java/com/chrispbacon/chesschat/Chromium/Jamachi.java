package com.chrispbacon.chesschat.Chromium;

import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.cef.CefApp;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Jamachi {

	private static final Color base = new Color(13, 13, 13);
	private static Rectangle previous;
	private static boolean toggle;
	private static Point initialClick;
	public static JFrame frame;

	public static void create(int port, boolean useOSR) throws IOException {
		JFrame frame = new JFrame();
		// frame.setIconImage(ImageIO.read(RunLevel.get("html/assets/Jamachi.png")));
		frame.setTitle("Jamachi");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (CefApp.getState() != CefApp.CefAppState.NONE) CefApp.getInstance().dispose();
				frame.dispose();
				System.exit(0);
			}
		});
		Container container = frame.getContentPane();
		container.setPreferredSize(new Dimension(350, 100));
		container.setLayout(new BorderLayout());
		VisualProgressHandler handler = new VisualProgressHandler();
		container.add(handler, BorderLayout.CENTER);
		ComponentResizer resizer = new ComponentResizer();
		resizer.registerComponent(frame);
		resizer.setSnapSize(new Dimension(10, 10));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		Path path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("jcef-bundle");
		try {
			System.out.println("http://127.0.0.1:" + port);
			Chromium chromium = new Chromium("http://127.0.0.1:" + port, path, useOSR, handler);
			frame.dispose();
			frame.setUndecorated(true);
			container.removeAll();
			container.setBackground(new Color(255, 255, 255));
			container.setPreferredSize(new Dimension(550, 550));
			JComponent component = (JComponent) container;
			component.setBackground(base);
			component.setBorder(new EmptyBorder(0, 5, 5, 5));
			JPanel move = getHeader(frame);
			// addWindowStyle(application, move, ImageIO.read(RunLevel.get("html/assets/some.png")));
			addWindowInteraction(move);
			move.setBackground(base);
			container.add(move, BorderLayout.NORTH);
			container.add(chromium.getBrowserUI(), BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
			System.err.println(e);
		}
		Jamachi.frame = frame;
	}

	public static final ActionListener MAXIMIZE = action -> {
		Jamachi.toggle = !Jamachi.toggle;
		if (Jamachi.toggle) {
			Jamachi.previous = Jamachi.frame.getBounds();
			Point location = Jamachi.frame.getLocation();
			GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			for (GraphicsDevice device : devices) {
				Rectangle screen = device.getDefaultConfiguration().getBounds();
				if (!screen.contains(location)) continue;
				Jamachi.frame.setBounds(screen);
			}
		} else {
			Jamachi.frame.setBounds(Jamachi.previous);
		}
	};

	//private static void addWindowStyle(Application application, JPanel move, BufferedImage image) {
	//	LogoComponent logo = new LogoComponent(image);
	//	JPanel style = new JPanel(new BorderLayout(5, 0));
	//	style.setBackground(base);
	//	style.add(logo, BorderLayout.WEST);
	//	SmoothLabel label = new SmoothLabel("JamAlong " + application.getVersion());
	//	label.setFont(new Font("Dialog", Font.BOLD, 18));
	//	label.setForeground(new Color(180, 180, 180));
	//	style.add(label, BorderLayout.CENTER);
	//	move.add(style, BorderLayout.WEST);
	//}

	private static void addWindowInteraction(JPanel move) {
		JPanel main = new JPanel(new GridLayout(0, 3));
		JamachiButton button1 = new JamachiButton(base, new Color(87, 85, 83));
		button1.addActionListener(listener -> Jamachi.frame.setState(JFrame.ICONIFIED));
		button1.setText("—");
		main.add(button1);
		JamachiButton button2 = new JamachiButton(base, new Color(87, 85, 83));
		button2.addActionListener(MAXIMIZE);
		button2.setText("\uD83D\uDDD6");
		main.add(button2);
		JamachiButton button3 = new JamachiButton(base, new Color(255, 0, 0));
		button3.addActionListener(listener -> System.exit(1));
		button3.setText("✖");
		main.add(button3);
		move.add(main, BorderLayout.EAST);
	}

	@NotNull
	private static JPanel getHeader(Frame source) {
		JPanel move = new JPanel();
		move.setLayout(new BorderLayout());
		move.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				Jamachi.initialClick = e.getPoint();
			}
		});
		move.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				final int thisX = source.getLocation().x;
				final int thisY = source.getLocation().y;
				final int xMoved = e.getX() - Jamachi.initialClick.x;
				final int yMoved = e.getY() - Jamachi.initialClick.y;
				final int X = thisX + xMoved;
				final int Y = thisY + yMoved;
				source.setLocation(X, Y);
			}
		});
		move.setBackground(new Color(255, 255, 255));
		move.setPreferredSize(new Dimension(0, 34));
		return move;
	}

}
