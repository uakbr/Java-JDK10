/*
 * @(#)Tumble.java	
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

import java.io.InputStream;
import awt.*;
import browser.*;
import net.www.html.*;

/**
 * Tumble class. This is a container for a list
 * of images that can be animated.
 *
 * @author 	James Gosling
 * @version 	1.17, 31 Jan 1995
 */
class Tumble {
    /**
     * The images.
     */
    Image imgs[];

    /**
     * The number of images actually loaded.
     */
    int nimgs = 0;
    int maxWidth = 0;
    int maxHeight = 0;
    int speed;

    /**
     * Load the images, from dir. The images are assumed to be
     * named T1.gif, T2.gif...
     * Once all images are loaded the applet is resized to the
     * maximum width and height.
     */
    Tumble(URL context, String dir, TumbleItem parent) {

	imgs = new Image[40];
	for (int i = 1; i < imgs.length; i++) {
	    Image im = parent.getImage(dir + "/T" + i + ".gif");

	    if (im == null) {
		break;
	    }

	    imgs[nimgs++] = im;
	    if (im.width > maxWidth) {
		maxWidth = im.width;
	    }
	    if (im.height > maxHeight) {
		maxHeight = im.height;
	    }
	}
	//parent.resize(maxWidth, maxHeight);
    }
}

/**
 * A simple Item class to play an image loop.  The "img" tag parameter
 * indicates what image loop to play.
 *
 * @author 	James Gosling
 * @version 	1.17, 31 Jan 1995
 */
public
class TumbleItem extends Applet implements Runnable {
    /**
     * The current loop slot.
     */
    int loopslot = 0;

    /**
     * The directory or URL from which the images are loaded
     */
    String dir;

    /**
     * The image loop.
     */
    Tumble loop;

    /**
     * The thread animating the images.
     */
    Thread kicker = null;

    /**
     * The length of the pause between revs.
     */
    int pause;

    int offset;
    int off;
    int speed;

    /**
     * Initialize the applet. Get attributes.
     */
    public void init() {
	String at = getAttribute("img");
	dir = (at != null) ? at : "images/tumble";
	at = getAttribute("pause");
	pause = (at != null) ? Integer.valueOf(at).intValue() : 3900;
	at = getAttribute("offset");
	offset = (at != null) ? Integer.valueOf(at).intValue() : 0;
	at = getAttribute("speed");
	speed = (at != null) ? (1000 / Integer.valueOf(at).intValue()) : 100;
    }

    /**
     * Run the image loop. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	loop = new Tumble(documentURL, dir, this);

	if (loop.nimgs > 1) {
	    if (offset < 0) {
		off = width - loop.maxWidth;
	    }
	    while (width > 0 && height > 0 && kicker != null) {
		if (++loopslot >= loop.nimgs) {
		    loopslot = 0;
		    off += offset;
		    if (off < 0) {
			off = width - loop.maxWidth;
		    } else if (off + loop.maxWidth > width) {
			off = 0;
		    }
		}
		repaint();
		Thread.sleep(speed + ((loopslot == loop.nimgs - 1) ? pause : 0));
	    }
	}
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	if ((loop != null) && (loop.imgs != null) &&
	    (loopslot < loop.nimgs) && (loop.imgs[loopslot] != null)) {
	    g.drawImage(loop.imgs[loopslot], off, 0);
	}
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (kicker == null) {
	    kicker = new Thread(this);
	    kicker.start();
	}
    }

    /**
     * Stop the applet. The thread will exit because kicker is set to null.
     */
    public void stop() {
	if (kicker != null) {
	    kicker.stop();
	    kicker = null;
	}
    }
}
