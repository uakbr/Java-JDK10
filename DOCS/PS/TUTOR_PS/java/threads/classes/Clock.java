/*
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

import browser.Applet;
import awt.Graphics;
import java.util.Date;

class Clock extends Applet implements Runnable {

    Thread clockThread;

    public void start() {
	if (clockThread == null) {
	    clockThread = new Thread(this, "Clock");
	    clockThread.start();
	}
    }
    public void run() {
	while (clockThread != null) {
	    repaint();
	    clockThread.sleep(1000);
	}
    }
    public void paint(Graphics g) {
	Date now = new Date();
	g.drawString(now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds(), 5, 10);
    }
    public void stop() {
	clockThread.stop();
	clockThread = null;
    }
}
