/*
 * @(#)ProgressData.java	1.4 95/05/13 Chris Warth
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

package net;

import browser.Observer;
import browser.Observable;
import net.www.html.URL;

public class ProgressData extends Observable {
    // We create a single instance of this class.
    // the Observer/Observable stuff only works with instances.
    //
    public static ProgressData pdata = new ProgressData();
    public static final int NEW = 0;
    public static final int CONNECTED = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;

    public int lastchanged = 0;
    public int what = 0;
    public ProgressEntry streams[] = new ProgressEntry[20];

    /*
     * Call this routine to register a new URL for the progress
     * window.  until it is marked as connected this entry will have
     * a busy indicator.
     */
    public synchronized void register(URL m) {
	ProgressEntry te;
	int i;

	//System.out.println("-- REGISTER: " + m.toExternalForm());
	for (i = 0; i < streams.length; i++) {
	    if (streams[i] == null) {
//		if (m != null) {
//		    System.out.println("register "+m.toExternalForm());
//		} else {
//		    System.out.println("Trying to register a null url!!!");
//		}
//
		te = new ProgressEntry(m, m.toExternalForm(), m.content_type) ;
		streams[i] = te;
		lastchanged = i;
		what = NEW;
		setChanged();
		notifyObservers();
		break;
	    }
	}
    }

    /*
     * Call this routine to register a new URL for the progress
     * window.  until it is marked as connected this entry will have
     * a busy indicator.
     */
    public synchronized void connected(URL m) {
	/* AVH: I made this a noop since it sends a CONNECT
	 * message when the first data arrives.
	ProgressEntry te;
	int i;
	System.out.println("-- CONNECTED: " + m.toExternalForm());

	for (i = 0; i < streams.length; i++) {
	    if (streams[i] != null && streams[i].key == m) {
		te = (ProgressEntry) streams[i];
		if (!te.connected()) {
		    te.setType(m.toExternalForm(), m.content_type);
		    lastchanged = i;
		    what = CONNECTED;
		    setChanged();
		    notifyObservers();
		}
		break;
	    }
	}
	*/
    }


    /*
     * Call this routine to unregister a new URL for the progress
     * window.  This will nuke the indicator from the ProgressWindow.
     */
    public synchronized void unregister(URL m) {
	int i;

	//System.out.println("-- UNREGISTER: " + m.toExternalForm());
	what = DELETE;
	for (i = 0; i < streams.length; i++) {
	    if (streams[i] != null && streams[i].key == m) {
//		ProgressEntry pe = (ProgressEntry) streams[i];
//		System.out.println("unregister "+pe.label);
		streams[i] = null;
		lastchanged = i;
		setChanged();
  	        notifyObservers();
		break;
	    }
	}
    }

    public synchronized void update(URL m, int total_read, int total_need) {
	int i;

	what = UPDATE;
	for (i = 0; i < streams.length; i++) {
	    if (streams[i] != null && streams[i].key == m) {
		ProgressEntry te = streams[i];
	        te.update(total_read, total_need);
		if (!te.connected()) {
		    te.setType(m.toExternalForm(), m.content_type);
		    lastchanged = i;
		    what = CONNECTED;
		    setChanged();
		    notifyObservers();
		}
		lastchanged = i;
		setChanged();
		if (te.read >= te.need && te.read != 0) {
		    streams[i] = null;
		    what = DELETE;
		}
  	        notifyObservers();
		break;
	    }
	}
    }
}
