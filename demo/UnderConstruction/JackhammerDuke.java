/*
 * %W% %E% Bob Weisblatt
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
import java.awt.*;
import java.net.*;

/**
 * A simple java.applet.Applet class to play an image loop.  The "img" tag parameter
 * indicates what image loop to play.
 *
 * @author 	Bob Weisblatt (major thievery from jag, maybe wholesale is a better word.)
 * @version 	1.07f, 19 Jan 1995
 */

public class JackhammerDuke extends java.applet.Applet implements Runnable {
    /**
     * The current loop slot.
     */
    int seqslot = 0;

    /**
     * The number of images
     */
    int nimgs = 4;

    /**
     * A strip of 4 images.
     */
    Image imgs;

    /**
     * The directory or URL from which the images are loaded
     */
    String dir;

    /**
     * The thread animating the images.
     */
    Thread kicker = null;

    /**
     * The length of the pause between revs.lkj 
     */
    int pause;

    /**
     * The current x position.
     */
    double x;

    int imgsWidth = 328;
    int imgsHeight = 90;

    /**
     * Run the image loop. This method is called by class Thread.
     * @see oak.lang.Thread
     *
     * getDocumentBase() is the URL of the document in which the applet is
     * embedded. It shouldn't be changed.
     *
     * dir is in net.www.html.Tag but is undocumented.
     */
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

	imgs = getImage(getCodeBase(), "images/jack.gif");

	if (imgs != null) {
	    x = (size().width - imgsWidth/nimgs) / 2;
	    while (size().width > 0 && size().height > 0 && kicker != null) {
		if (seqslot == 0) {
		    play(getCodeBase(), "audio/jackhammer-short.au");
		}

		repaint();
		try {Thread.sleep((seqslot == sequence.length -1) ? 500 : 100);} catch (InterruptedException e){}
		seqslot = (seqslot + 1) % sequence.length;
	    }
	}

    }

    private int sequence[] = { 2, 1, 0, 1, 0, 1, 0, 1, 2, 1, 0, 2, 0, 1, 0, 2, 3};

    /**
     * Paint the current frame.
     */
    boolean erase;

    public void update(Graphics g) {
	if (erase || (sequence[seqslot] == 3)) {
	    g.setColor(Color.lightGray);
	    g.fillRect(0, 0, size().width, size().height);
	    erase = false;
	}
	paint(g);
    }
    public void paint(Graphics g) {
	int loopslot = sequence[seqslot];
	if ((imgs != null) && (loopslot < nimgs)) {
	    int w = imgsWidth / nimgs;
	    x = Math.max(0, Math.min(size().width - w, x + Math.random() * 4 - 2));
	    
	    g.clipRect((int)x, 0, w, imgsHeight);
	    g.drawImage(imgs, (int)x - loopslot * w, 0, this);
	    erase = (loopslot == 3);
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
}
