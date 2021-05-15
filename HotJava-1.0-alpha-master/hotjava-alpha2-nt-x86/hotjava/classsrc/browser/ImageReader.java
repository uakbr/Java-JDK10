/*
 * @(#)ImageReader.java	1.11 94/12/26 Jonathan Payne
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

import awt.*;
import java.util.*;
import java.io.*;
import net.www.html.*;

class ImageFetcher implements Runnable {
    ImageReader		master;
    ImageHandle		h;

    ImageFetcher(ImageReader master, ImageHandle h) {
	this.master = master;
	this.h = h;
    }

    public void run() {
	try {
	    h.fetchImage();
	} catch(Exception e) {
	    System.out.println("Error " + e + " reading " + h);
	}
	master.imageFetcherCompletes(h);
    }
}

/**
 * Class ImageReader is a thread which sits around waiting for
 * requests to fetch images in behalf of ImageHandles.  It performs
 * fetches in background threads, and allows up to
 * <b>ImageReader.simultaneousFetcherLimit</b> requests at the same
 * time.  ImageReader is created and managed by instances of WRWindow.
 * @see WRWindow
 * @version 1.11, 26 Dec 1994
 * @author Jonathan Payne
 */

public class ImageReader extends Thread {
    static int	simultaneousFetcherLimit = 4;

    Vector handles = new Vector();
    URL ThisURL;
    int	fetcherCount = 0;
    boolean paused = true;

    public ImageReader () {
	setName("Background Image Reader");
	start();
    }

    synchronized boolean emptyQueue() {
	return (fetcherCount == 1 && handles.size() == 0);
    }

    synchronized void waitForRequest() {
	while (handles.size() == 0) {
	    wait(1000);
	}
    }

    private synchronized ImageHandle getNextRequest() {
	ImageHandle h;

	if (handles.size() == 0) {
	    return null;
	}
	h = (ImageHandle) handles.elementAt(0);
	handles.removeElementAt(0);
	return h;
    }

    synchronized void flushPending() {
	handles.setSize(0);
    }

    /*
     * Fork a thread to fetch a single image.  If there are already
     * some maximum number of image fetched outstanding, this
     * routine blocks waiting for one of the others to complete.
     */
    private synchronized void forkImageFetcher(ImageHandle h) {
	while (fetcherCount >= simultaneousFetcherLimit) {
	    wait(1000);
	}
	fetcherCount += 1;
	Thread fetcher = new Thread(new ImageFetcher(this, h));
	fetcher.setName("ImageFetcher " + fetcherCount);
	fetcher.start();
    }

    synchronized void imageFetcherCompletes(Observable o) {
	o.notifyObservers();

	if (fetcherCount-- == simultaneousFetcherLimit) {
	    notifyAll();
	}
    }

    /**
     * Add an ImageHandle to the background image reader.  This
     * causes the specified ImageHandle to have its image loaded by a
     * background thread.
     * @param h	the ImageHandle to load.
     */
    synchronized void add(ImageHandle h) {
	handles.addElement(h);
	notifyAll();
    }

    /**
     * Remove an ImageHandle from the image read queue. It won't
     * cancel the request if it has already started.
     */
    synchronized void remove(ImageHandle h) {
	handles.removeElement(h);
    }

    synchronized int imagesPending() {
	return handles.size() + fetcherCount;
    }


    public void run() {
	try {
	    ImageHandle h;

	    setPriority(Thread.MIN_PRIORITY+1);
	    while (true) {
		waitForRequest();
		
		h = getNextRequest();

		if (h != null) {
		    try {
			forkImageFetcher(h);
		    } catch(Exception e) {
//		    System.out.print("Error in image reader: ");
//		    e.printStackTrace();
		    }
		}
	    }
	} catch (ThreadDeath d) {
	    for (Enumeration e = handles.elements() ; e.hasMoreElements() ;) {
		ImageHandle h = (ImageHandle)e.nextElement();
		System.out.println("CANCELLING: " + h);
		h.setImage("Image fetch cancelled, try again");
	    }
	}
    }
}
