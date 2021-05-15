/*
 * @(#)ProgressBusy.java	1.2 95/05/11 Chris Warth
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

import net.ProgressEntry;
import net.ProgressData;

class ProgressBusy extends Thread {
    ProgressWindow target;

    ProgressBusy(ProgressWindow w) {
	target = w;
	start();
    }

    synchronized void wakeUp() {
	notify();
    }

    synchronized void waitForWork(boolean foundwork) {
	// if we found work the last time around we only wait a short time, 
	// otherwise we wait forever for some new work to arrive.
	// This is to avoid having this thread suck up resources when there
	// is nothing to draw.
	//
        wait((foundwork ? 100 : 0)); 
    }



    public void run() {
	ProgressEntry plist[] = ProgressData.pdata.streams;
	int pos = 0;
	int oldpos = 0;
	int direction = +5;

	setName("Progress Busy Loop");

	while (true) {
	    boolean foundwork = false;
	    for (int i = 0 ; i < plist.length; i++) {
		ProgressEntry pe = plist[i];
		    
		if (pe != null && pe.connected == false) {
		    target.busyPaint(i, oldpos, pos);
		    foundwork = true;
		}
	    }
	    waitForWork(foundwork);
	    oldpos = pos;
	    pos += direction;
	    if (pos > 100) {
		pos = 100 - (pos - 100);
		direction = -direction;
	    } else if (pos < 0) {
		pos = -pos;
		direction = -direction;
	    }

	}
    }
}
