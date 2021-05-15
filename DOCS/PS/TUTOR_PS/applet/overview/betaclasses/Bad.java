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
import java.awt.Graphics;

public class Bad extends java.applet.Applet {

    static final int NUMLOOPS = 2000000;
    int loop = 0;
    boolean doneInitializing = false;
    String message = null;
    Thread loopThread = null;

    public void init() {
	resize(500, 20);
	while (loop < NUMLOOPS) {
	    if ((++loop%50000)==0) {
		message = "Bad: Initialization loop #"
			  + loop + " of " + NUMLOOPS;
		getAppletContext().showStatus(message);
		repaint();
	    }
	}
	doneInitializing = true;
	repaint();
    }

    /* The paint() method can't be called until init() has exited. */
    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width - 1, size().height - 1);
	if (message == null) 
	    g.drawString("Bad: ", 5, 15);
	else if (!doneInitializing) 
	    g.drawString(message, 5, 15);
	else 
	    g.drawString("Bad: Done initializing.", 5, 15);
    }
}
