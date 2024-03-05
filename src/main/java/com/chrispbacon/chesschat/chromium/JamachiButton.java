package com.chrispbacon.chesschat.chromium;

import javax.swing.*;
import java.awt.*;

public class JamachiButton extends JButton {

	private final Color base, hover;

	public JamachiButton(Color base, Color hover) {
		this.setPreferredSize(new Dimension(45, 30));
		this.setForeground(new Color(180, 180, 180));
		this.setContentAreaFilled(false);
		this.setForeground(Color.WHITE);
		this.setFocusPainted(false);
		this.setBackground(base);
		this.setBorder(null);
		this.hover = hover;
		this.base = base;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (getModel().isPressed()) {
			g.setColor(hover.brighter());
		} else if (getModel().isRollover()) {
			g.setColor(hover);
		} else {
			g.setColor(base);
		}
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}
