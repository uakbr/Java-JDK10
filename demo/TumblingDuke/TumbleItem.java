/*
 * @(#)Tumble.java	
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://java.sun.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://java.sun.com/licensing.html for further important licensing
 * information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

import java.io.InputStream;
import java.applet.Applet;
import java.awt.*;
import java.net.*;

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
    int nimgs;

    /**
     * The images.
     */
    Image imgs[];
    int maxWidth;

    /**
     * Initialize the applet. Get attributes.
     */
    public void init() {
	String at = getParameter("img");
	dir = (at != null) ? at : "images/tumble";
	at = getParameter("pause");
	pause = (at != null) ? Integer.valueOf(at).intValue() : 3900;
	at = getParameter("offset");
	offset = (at != null) ? Integer.valueOf(at).intValue() : 0;
	at = getParameter("speed");
	speed = (at != null) ? (1000 / Integer.valueOf(at).intValue()) : 100;
	at = getParameter("nimgs");
	nimgs = (at != null) ? Integer.valueOf(at).intValue() : 16;
	at = getParameter("maxwidth");
	maxWidth = (at != null) ? Integer.valueOf(at).intValue() : 0;
    }

    /**
     * Run the image loop. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread.currentThread().setPriority(Thread.NORM_PRIORITY-1);
	imgs = new Image[nimgs];
	for (int i = 1; i < nimgs; i++) {
	    imgs[i] = getImage(getDocumentBase(), dir + "/T" + i + ".gif");
	}

	Dimension d = size();
	if (nimgs > 1) {
	    if (offset < 0) {
		off = d.width - maxWidth;
	    }
	    while (kicker != null) {
		//System.out.println("frame = " +  loopslot);
		if (++loopslot >= nimgs) {
		    loopslot = 0;
		    off += offset;
		    if (off < 0) {
			off = d.width - maxWidth;
		    } else if (off + maxWidth > d.width) {
			off = 0;
		    }
		}
		repaint();
		try {
		    Thread.sleep(speed + ((loopslot == nimgs - 1) ? pause : 0));
		} catch (InterruptedException e) {
		    break;
		}
	    }
	}
    }

    public boolean imageUpdate(Image img, int flags,
			       int x, int y, int w, int h) {
	if ((flags & (SOMEBITS|FRAMEBITS|ALLBITS)) != 0) {
	    if ((imgs != null) && (loopslot < nimgs) && (imgs[loopslot] == img)) {
		repaint(100);
	    }
	}
	return (flags & (ALLBITS|ERROR)) == 0;
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	//System.out.println("paint");
	if ((imgs != null) && (loopslot < nimgs) && (imgs[loopslot] != null)) {
	    g.drawImage(imgs[loopslot], off, 0, this);
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
