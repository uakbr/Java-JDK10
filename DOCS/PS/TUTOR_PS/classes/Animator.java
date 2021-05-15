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
/*
 * @(#)Animator.java	1.3 95/09/29 Herb Jellinek
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

import java.io.InputStream;
import awt.*;
import browser.Applet;
import browser.audio.*;
import java.util.Vector;
import java.io.File;
import net.www.html.*;


/**
 * An applet that plays a sequence of images, as a loop or a one-shot.
 * Can have a soundtrack and/or sound effects tied to individual frames.
 *
 * @author James Gosling (original ImageLoopItem.java)
 * @author Herb Jellinek
 * @version 1.3, 29 Sep 1995
 */

class Animator extends Applet implements Runnable {
    
    /**
     * The images.
     */
    Vector images;

    /**
     * Order in which to display them, time and sound for each one.
     */
    Vector order;

    /**
     * Largest width.
     */
    int maxWidth = 0;

    /**
     * Largest height.
     */
    int maxHeight = 0;

    /**
     * The directory or URL from which the images are loaded
     */
    String dir;

    /**
     * The thread animating the images.
     */
    Thread engine = null;

    /**
     * The current loop slot - index into 'order.'
     */
    int frameNum = 0;

    /**
     * The default number of milliseconds to wait between frames.
     */
    public static final int defaultPause = 3900;
    
    /**
     * The global delay between images, which can be overridden by
     * the PAUSE attribute.
     */
    int globalPause = defaultPause;

    /**
     * The soundtrack.
     */
    InputStream audioStream;

    /**
     * Whether or not the thread has been paused by the user.
     */
    boolean userPause = false;

    /**
     * Repeat the animation?  If false, just play it once.
     */
    boolean repeat;

    /**
     * Can this version of the browser do double buffered graphics?
     */
    boolean canDoubleBuffer;
    
    /**
     * The offscreen image, used in double buffering
     */
    Image offScrImage;

    /**
     * The offscreen graphics context, used in double buffering
     */
    Graphics offScrGC;

    /**
     * Can we paint yet?
     */
    boolean loaded = false;

    /**
     * "Loading" message.
     */
    String loadingMessage = "Wait...";
    
    /**
     * Draw the loading message, or draw the background?
     */
    boolean showMessage = true;
    
    boolean debug = false;

    /**
     * Load the images.
     * @param dir directory to load from. The images are assumed
     *   to be named T1.gif, T2.gif...
     *
     * Sets maxWidth, maxHeight to be those of the largest image.
     * Sets images to contain the GIFs.
     * If order vector is null, create a default one.
     */
    void loadImages(URL context, String dir) {
	images = new Vector(10);
	for (int i = 1; ; i++) {
	    showLoadingMsg();
	    Image im = getImage(dir+File.separator+"/T"+i+".gif");

	    if (im == null) {
		break;
	    }

	    if (debug) {
	        System.out.print("Got image T"+i+".gif; ");
	    }

	    images.addElement(im);
	    if (debug) {
	        System.out.println("made it image "
				    + (images.size() - 1) + ".");
	    }
	    if (im.width > maxWidth) {
		maxWidth = im.width;
	    }
	    if (im.height > maxHeight) {
		maxHeight = im.height;
	    }
	}
	if (order == null) {
	    order = new Vector(images.size());
	    for (int i = 0; i < images.size(); i++) {
		order.addElement(new FrameSpec(i, globalPause, null));
	    	if (debug) {
	            System.out.println("Added image " + i + 
				       " to order Vector at position "
				       + (order.size() - 1) + ".");
	        }
	    }
	}
    }

    /**
     * Parse the ORDER attribute.  It looks like
     * 1|2|3|4|5, etc., where each number (item) refers to a source image.
     * Each item can also be of the form nn:mm, where mm is the number
     * of milliseconds to show that image, or nn:mm@xxxxx, where xxxx is the
     * URL for a sound effect to accompany that image.
     */
    void parseOrder(String attr) {
	if (attr != null) {
	    order = new Vector(10);
	    for (int i = 0; i < attr.length(); ) {
		int next = attr.indexOf('|', i);
		if (next == -1) next = attr.length();
		parseItem(attr.substring(i, next));
		i = next + 1;
	    }
	}
    }

    void parseItem(String item) {
	// Cases: nothing but a frame number
	//        imageno:pause
	//        imageno:pause@audio
	//        imageno@audio

	int pause = globalPause;
	int imageNum;
	String audio = null;
	
	int colonPos = item.indexOf(':');
	int atPos = item.indexOf('@');

	if (atPos > 0) {
	    if (colonPos < 0) {
		imageNum = Integer.parseInt(item.substring(0, atPos)) - 1;
		audio = item.substring(atPos + 1);
	    } else {
		imageNum = Integer.parseInt(item.substring(0, colonPos)) - 1;
		pause = Integer.parseInt(item.substring(colonPos + 1, atPos));
		audio = item.substring(atPos + 1);
	    }
	} else {
	    if (colonPos < 0) {
		imageNum = Integer.parseInt(item) - 1;
	    } else {
		imageNum = Integer.parseInt(item.substring(0, colonPos)) - 1;
		pause = Integer.parseInt(item.substring(colonPos + 1));
	    }
	}

	if (audio != null) {
	    tellLoadingMsg(audio);
	}
	order.addElement(new FrameSpec(imageNum, pause,
				       (audio == null) ? null :
				       getAudioData(new URL(documentURL,
							    audio))));
	if (debug) {
	    System.out.println("Added image " + imageNum + 
				       " to order Vector at position "
				       + (order.size() - 1) + ".");
	}
    }

    
    /**
     * Initialize the applet.  Get attributes.
     */
    public void init() {
	String attr = getAttribute("IMG");
	dir = (attr != null) ? attr : "doc:/demo/images/duke";
	
	attr = getAttribute("PAUSE");
	globalPause = (attr != null) ? Integer.parseInt(attr) : defaultPause;

	attr = getAttribute("REPEAT");
	repeat = (attr == null) ? true : (attr.equalsIgnoreCase("yes") ||
					  attr.equalsIgnoreCase("true"));
	
	attr = getAttribute("ORDER");
	parseOrder(attr);

	attr = getAttribute("AUDIOLOOPS");
	boolean audioLoops =
	    (attr == null) ? false : (attr.equalsIgnoreCase("yes") ||
				      attr.equalsIgnoreCase("true"));
	attr = getAttribute("AUDIO");
	if (attr != null) {
	    URL url = new URL(documentURL, attr);
	    showLoadingMsg();
	    if (audioLoops) {
		audioStream = getContinuousAudioStream(url);
	    } else {
		audioStream = getAudioStream(url);
	    }
	}

	canDoubleBuffer = true;
	try {
	    // for when/if browser.hotjava goes away
	    canDoubleBuffer = !browser.hotjava.version.equals("1.0 alpha2");
	} catch (Exception e) { }
    }

    void tellLoadingMsg(String file) {
	showStatus("Loading file "+file);
    }

    void showLoadingMsg() {
	repaint();
	showMessage = !showMessage;
    }

    void drawLoadingMessage(Graphics g) {
	if (showMessage) {
	    g.drawString(loadingMessage, 0, height / 2);
	} else {
	    g.clearRect(0, 0, width, height);
	}
    }
	    

    void startPlaying() {
	startPlaying(audioStream);
    }

    void stopPlaying() {
	stopPlaying(audioStream);
    }

    /**
     * Run the animation. This method is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
	Thread me = Thread.currentThread();
	me.setPriority(Thread.MIN_PRIORITY);

	loadImages(documentURL, dir);
	
	resize(maxWidth, maxHeight);
	if (canDoubleBuffer) {
	    offScrImage = createImage(maxWidth, maxHeight);
	    offScrGC = new Graphics(offScrImage);
	    offScrGC.setForeground(Color.lightGray);
	}

	loaded = true;

	if (userPause) {
	    return;
	}

	if (frameNum < order.size()) {
	    startPlaying();
	}

	try {	
	    if (images.size() > 1) {
		while (width > 0 && height > 0 && engine == me) {
		    if (frameNum >= order.size()) {
			if (!repeat) {
			    return;
			}
			frameNum = 0;
		    }
		    repaint();
		    FrameSpec spec = (FrameSpec)order.elementAt(frameNum++);
		    if (spec.audio != null) {
			play(spec.audio);
		    }
		    Thread.sleep(100 + spec.pause);
		}
	    }
	} finally {
	    stopPlaying();
	}
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	update(g);
    }

    public void update(Graphics g) {
	if (! loaded) {
	    drawLoadingMessage(g);
	} else {
	    if (images != null) {
		if (frameNum < order.size()) {
		    if (canDoubleBuffer) {
			offScrGC.fillRect(0, 0, width, height);
		    } else {
			g.clearRect(0, 0, width, height);
		    }
		    FrameSpec spec = (FrameSpec)order.elementAt(frameNum);
		    if (debug) {
			System.out.print("Drawing frame "+frameNum+": ");
		    }
		    if (spec.num < images.size()) {
		        if (debug) {
			    System.out.println("image "+spec.num+".");
		        }
			Image image = (Image)images.elementAt(spec.num);
			if (canDoubleBuffer) {
			    offScrGC.drawImage(image, 0, 0);
			    g.drawImage(offScrImage, 0, 0);
			} else {
			    g.drawImage(image, 0, 0);
			}
		    }
		} else {
		    // no more animation, but need to draw something
		    if (debug) {
			System.out.print("No more animation; ");
			System.out.println("drawing image "+ 0 + ".");
		    }
		    g.drawImage((Image)images.elementAt(0), 0, 0);
		}
	    }
	}
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
	if (engine == null) {
	    engine = new Thread(this);
	    engine.start();
	}
    }

    /**
     * Stop the insanity, um, applet.
     */
    public void stop() {
	if (engine != null && engine.isAlive()) {
	    engine.stop();
	}
	engine = null;
    }

    /**
     * Pause the thread when the user clicks the mouse in the applet.
     * If the thread has stopped (as in a non-repeat performance), restart it.
     */
    public void mouseDown(int x, int y) {
	if (loaded) {
	    if (engine != null && engine.isAlive()) {
		if (userPause) {
		    engine.resume();
		    startPlaying();
		} else {
		    engine.suspend();
		    stopPlaying();
		}
		userPause = !userPause;
	    } else {
		userPause = false;
		frameNum = 0;
		engine = new Thread(this);
		engine.start();
	    }
	}
    }
}
