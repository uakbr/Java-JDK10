/*
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 */

import java.awt.*;
import java.util.StringTokenizer;

/**
 * I love blinking things.
 *
 * @author Arthur van Hoff
 */
public class Blink extends java.applet.Applet implements Runnable {
    Thread blinker;
    String lbl;
    Font font;
    int speed;

    public void init() {
	font = new java.awt.Font("TimesRoman", Font.PLAIN, 24);
	String att = getParameter("speed");
	speed = (att == null) ? 400 : (1000 / Integer.valueOf(att).intValue());
	att = getParameter("lbl");
	lbl = (att == null) ? "Blink" : att;
    }
    
    public void paint(Graphics g) {
	int x = 0, y = font.getSize(), space;
	int red = (int)(Math.random() * 50);
	int green = (int)(Math.random() * 50);
	int blue = (int)(Math.random() * 256);
	Dimension d = size();

	g.setColor(Color.black);
	g.setFont(font);
	FontMetrics fm = g.getFontMetrics();
	space = fm.stringWidth(" ");
	for (StringTokenizer t = new StringTokenizer(lbl) ; t.hasMoreTokens() ; ) {
	    String word = t.nextToken();
	    int w = fm.stringWidth(word) + space;
	    if (x + w > d.width) {
		x = 0;
		y += font.getSize();
	    }
	    if (Math.random() < 0.5) {
		g.setColor(new java.awt.Color((red + y * 30) % 256, (green + x / 3) % 256, blue));
	    } else {
		g.setColor(Color.lightGray);
	    }
	    g.drawString(word, x, y);
	    x += w;
	}
    }

    public void start() {
	blinker = new Thread(this);
	blinker.start();
    }
    public void stop() {
	blinker.stop();
    }
    public void run() {
	while (true) {
	try {Thread.currentThread().sleep(speed);} catch (InterruptedException e){}
	    repaint();
	}
    }
}
