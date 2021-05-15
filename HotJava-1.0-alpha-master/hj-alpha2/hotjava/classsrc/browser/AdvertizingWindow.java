/*
 * @(#)AdvertizingWindow.java	1.35 95/03/20 James Gosling
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package browser;

import java.io.BufferedInputStream;
import awt.*;
import net.www.html.URL;
import java.util.Vector;

class AdvertisingItem {
    String  blurb;
    String  target;

    AdvertisingItem (String b, String t) {
	blurb = b;
	target = t;
    }
}

/**
 * The advertizing window class
 * @author	James Gosling
 * @version	1.35, 20 Mar 1995
 */
class AdvertizingWindow extends DisplayItemWindow implements Runnable {
    /**
     * True if the mouse is over this window.
     */
    public boolean mouseIn = false;

    /**
     * Where the ad list is fetched, initially.
     */
    public static String adBase;

    /**
     * The thread that flips the images.
     */
    private Thread kicker = null;

    /**
     * The current ad.
     */
    private AdvertisingItem currentAd;

    /**
     * The base URL for the ads.
     */
    URL ad_base;

    /**
     * The hotjava app
     */
    hotjava	app;

    /**
     * Create and AdvertizingWindow.
     */
    public AdvertizingWindow(Window itsParent, String name, Color bg, hotjava m) {
	super(itsParent, name);
	setMargin(0);
	setInsets(0,0,0,0);
	width = 140;
	height = 80;
	app = m;
    }

    /**
     * Wait for the window to be mapped before doing any graphics
     * operations.
     */
    public void map() {
	super.map();
	start();
    }

    /**
     * Display a list of ads.
     */
    public synchronized void display(Vector ads) {
	ImageDisplayItem di = new ImageDisplayItem(null);
	addItem(di);
	while (ads.size() > 0) {
	    int i;

	    for (i = 0; i < ads.size(); ) {
		currentAd = (AdvertisingItem) ads.elementAt(i);

		if (currentAd.blurb != null) {
		    try {
			Image img = di.getImage();
			URL url = new URL(ad_base, currentAd.blurb);
			DIBitmap bm = (DIBitmap)url.getContent();

			di.move((width - bm.width) / 2,
				(height - bm.height) / 2);

			di.setImage(bm);

			/* if we set a new image, destroy the old one */
			if (img != null) {
			    img.dispose();
			}
			paint();
			wait(15000);
			i += 1;
		    } catch(Exception e) {
			e.printStackTrace();
			currentAd.blurb = null;
			ads.removeElementAt(i);
		    }
		}
	    }
	}
    }

    /**
     * Run the thread.
     */
    public void run() {
	if ((adBase == null) || (adBase.length() == 0)) {
	    return;
	}

	Vector	ads = new Vector();
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	try {
	    StringBuffer sb = new StringBuffer(60);
	    String s1 = null, s2 = null;
	    ad_base = new URL(null, adBase);
	    BufferedInputStream si = new BufferedInputStream(ad_base.openStreamInteractively());
	    int c;

	    while ((c = si.read()) != -1) {
		switch (c) {
		  default:
		    sb.appendChar(c);
		    break;
		  case ' ':
		  case '\t':
		    if (s1 == null && sb.length() > 0) {
			s1 = sb.toString();
			sb.setLength(0);
			sb.copyWhenShared();
		    }
		    break;
		  case '\n':
		    if (sb.length() > 0) {
			s2 = sb.toString();
		    } else
			s2 = null;
		    ads.addElement(new AdvertisingItem(s1, s2));
		    sb.setLength(0);
		    sb.copyWhenShared();
		    s1 = null;
		    s2 = null;
		    break;
		}
	    }
	    si.close();
	    display(ads);
	} catch(Exception e) {
	    e.printStackTrace();
	}
	kicker = null;
    }

    /**
     * Start the ads.
     */
    public synchronized void start() {
	if (kicker == null) {
	    kicker = new Thread(this);
	    kicker.start();
	}
    }

    /**
     * Stop the ads.
     */
    public synchronized void stop() {
	if (kicker != null) {
	    kicker.stop();
	    kicker = null;
	}
    }

    void setAdBase(String base) {
	adBase = base;
	if (kicker != null) {
	    kicker.stop();
	    kicker = null;
	}
	clearItems();
	paint();
	start();
    }

    private void feedback(boolean on) {
	if (on) {
	    if (currentAd != null && currentAd.target != null) {
		setForeground(Color.blue);
		graphics.drawRect(0, 0, width-2, height-2);
		URL u = new URL(ad_base, currentAd.target);
		app.setMessage(u.toExternalForm());
	    }
	} else {
	    setForeground(background);
	    graphics.drawRect(0, 0, width-2, height-2);
	    app.setMessage("");
	}
    }

    public void mouseEnter(Event e) {
	feedback(true);
	mouseIn = true;
    }

    public void mouseLeave(Event e) {
	feedback(false);
	mouseIn = false;
    }

    public void paint() {
	super.paint();
	if (mouseIn) {
	    feedback(true);
	}
    }

    /**
     * Go to the current ad.
     */
    public void mouseDown(Event e) {
	if (currentAd != null && currentAd.target != null) {
	    app.doc.pushURL(new URL(ad_base, currentAd.target));
	}
    }

    public Dimension minDimension() {
	return getPreferredSize();
    }

    public Dimension getPreferredSize() {
	return new Dimension(140, 80);
    }
}
