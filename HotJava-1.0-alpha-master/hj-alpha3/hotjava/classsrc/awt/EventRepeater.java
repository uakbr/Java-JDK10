/*
 * @(#)EventRepeater.java	1.5 95/01/31 Patrick Naughton
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

package awt;

/**
 *	Thread to repeatedly call an event handler.
 *
 * @version 1.5 31 Jan 1995
 * @author Patrick Naughton
 */
public class EventRepeater extends Thread {
    private EventHandler handler;
    private Event event;
    private int delay = 10;
    private int init_wait = 400;
    private boolean dead = false;

    public EventRepeater (EventHandler eh, Event e, int iw, int d) {
	handler = eh;
	event = (Event) e.clone();
	init_wait = iw;
	delay = d;
	start();
    }

    public EventRepeater (EventHandler eh, Event e) {
	this(eh, e, 50, 500);
    }

    public void setDelay(int d) {
	delay = d;
    }

    public void run() {
	sleep(init_wait);
	while (!dead) {
	    handler.handleEvent(event);
	    sleep(delay);
	}
    }

    public void die() {
	dead = true;
    }
}

