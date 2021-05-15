/*
 * @(#)ImageLoopItem.java	1.22 95/03/28 James Gosling
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
 * ImageLoop class. This is a container for a list
 * of images that can be animated.
 *
 * @author 	James Gosling
 * @version 	1.22, 28 Mar 1995
 */
class ImageLoop {
    /**
     * The images.
     */
    Image imgs[];

    /**
     * The number of images actually loaded.
     */
    int nimgs = 0;

    /**
     * Load the images, from dir. The images are assumed to be
     * named T1.gif, T2.gif...
     * Once all images are loaded the applet is resized to the
     * maximum width and height.
     */
    ImageLoop(URL context, String dir, ImageLoopItem parent) {
	int maxWidth = 0;
	int maxHeight = 0;

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
	parent.resize(maxWidth, maxHeight);
    }
}

/**
 * A simple Item class to play an image loop.  The "img" tag parameter
 * indicates what image loop to play.
 *
 * @author 	James Gosling
 * @version 	1.22, 28 Mar 1995
 */
public
class ImageLoopItem extends Applet implements Runnable {
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
    ImageLoop loop;

    /**
     * The thread animating the images.
     */
    Thread kicker = null;

    /**
     * The length of the pause between revs.
     */
    int pause;

    /**
     * Whether or not the thread has been paused by the user.
     */
    boolean threadSuspended = false;

    /**
     * The offscreen image.
     */
    Image	im;

    /**
     * The offscreen graphics context
     */
    Graphics	offscreen;

    /**
     * Initialize the applet. Get attributes.
     */
    public void init() {
	String at = getAttribute("img");
	dir = (at != null) ? at : "doc:/demo/images/duke";
	at = getAttribute("pause");
	pause = (at != null) ? 0 : 3900;
    }

    /**
     * Run the image loop. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	loop = new ImageLoop(documentURL, dir, this);

	if (loop.nimgs > 1) {
	    while (width > 0 && height > 0 && kicker != null) {
		if (++loopslot >= loop.nimgs) {
		    loopslot = 0;
		}
		repaint();
		Thread.sleep(100 + ((loopslot == 0) ? pause : 0));
	    }
	}
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	update(g);
    }
    public void update(Graphics g) {
	if ((loop != null) && (loop.imgs != null) &&
	    (loopslot < loop.nimgs) && (loop.imgs[loopslot] != null)) {
	    if (im == null) {
		im = createImage(width, height);
		offscreen = new Graphics(im);
		offscreen.setForeground(Color.lightGray);
	    }
	    offscreen.fillRect(0, 0, width, height);
	    offscreen.drawImage(loop.imgs[loopslot], 0, 0);
	    g.drawImage(im, 0, 0);
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
	kicker = null;
    }

    /**
     * Pause the thread when the user clicks the mouse in the applet.
     */
    public void mouseDown(int x, int y) {
        if (threadSuspended)
            kicker.resume();
	else
            kicker.suspend();
        threadSuspended = !threadSuspended;
    }

}
