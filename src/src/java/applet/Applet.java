/*
 * @(#)Applet.java	1.29 95/12/14 Arthur van Hoff
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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
package java.applet;

import java.awt.*;
import java.awt.image.ColorModel;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Base applet class. 
 *
 * @version 	1.29, 12/14/95
 *
 * @author Chris Warth
 * @author Arthur van Hoff
 */
public class Applet extends Panel {
    private AppletStub stub;

    /**
     * Sets the applet stub. This is done by automatically by the system.
     */
    public final void setStub(AppletStub stub) {
	this.stub = (AppletStub)stub;
    }

    /**
     * Returns true if the applet is active. An applet is marked active
     * just before the start method is called.
     * @see #start
     */
    public boolean isActive() {
	return stub.isActive();
    }
    
    /**
     * Gets the document URL. This is the URL of the document in which
     * the applet is embedded.
     * @see #getCodeBase
     */
    public URL getDocumentBase() {
	return stub.getDocumentBase();
    }

    /**
     * Gets the base URL. This is the URL of the applet itself. 
     * @see #getDocumentBase
     */
    public URL getCodeBase() {
	return stub.getCodeBase();
    }

    /**
     * Gets a parameter of the applet.
     */
     public String getParameter(String name) {
	 return stub.getParameter(name);
     }

    /**
     * Gets a handle to the applet context. The applet context
     * lets an applet control the applet's environment which is
     * usually the browser or the applet viewer.
     */
    public AppletContext getAppletContext() {
	return stub.getAppletContext();
    }

    /**
     * Requests that the applet be resized.
     */
    public void resize(int width, int height) {
	Dimension d = size();
	if ((d.width != width) || (d.height != height)) {
	    super.resize(width, height);
	    if (stub != null) {
		stub.appletResize(width, height);
	    }
	}
    }

    /**
     * Requests thatthe applet be resized.
     */    
    public void resize(Dimension d) {
	resize(d.width, d.height);
    }

    /**
     * Shows a status message in the applet's context.
     */
    public void showStatus(String msg) {
	getAppletContext().showStatus(msg);
    }

    /**
     * Gets an image given a URL. Note that this method
     * always returns an image object immediatly, even if 
     * the image does not exist. The actual image data is 
     * loaded when it is first needed.
     */
    public Image getImage(URL url) {
	return getAppletContext().getImage(url);
    }

    /**
     * Gets an image relative to a URL. This methods returns
     * immediately, even if the image does not exist. The actual
     * image data is loaded when it is first needed.
     * 
     * @see #getImage
     */
    public Image getImage(URL url, String name) {
	try {
	    return getImage(new URL(url, name));
	} catch (MalformedURLException e) {
	    return null;
	}
    }

    /**
     * Gets an audio clip. 
     */
    public AudioClip getAudioClip(URL url) {
	return getAppletContext().getAudioClip(url);
    }

    /**
     * Gets an audio clip. 
     * @see #getAudioClip
     */
    public AudioClip getAudioClip(URL url, String name) {
	try {
	    return getAudioClip(new URL(url, name));
	} catch (MalformedURLException e) {
	    return null;
	}
    }

    /**
     * Returns a string containing information about
     * the author, version and copyright of the applet.
     */
    public String getAppletInfo() {
	return null;
    }

    /**
     * Returns an array of strings describing the
     * parameters that are understood by this
     * applet. The array consists of sets of three strings:
     * name/type/description. For example:
     * <pre>
     * 	String pinfo[][] = {
     *	  {"fps",    "1-10",    "frames per second"},
     *	  {"repeat", "boolean", "repeat image loop"},
     *	  {"imgs",   "url",     "directory in which the images live"}
     *	};
     * </pre>
     */
    public String[][] getParameterInfo() {
	return null;
    }

    /**
     * Plays an audio clip. Nothing happens if the audio clip could
     * not be found.
     */
    public void play(URL url) {
	AudioClip clip = getAudioClip(url);
	if (clip != null) {
	    clip.play();
	}
    }

    /**
     * Plays an audio clip. Nothing happens if the audio clip could
     * not be found.
     */
    public void play(URL url, String name) {
	AudioClip clip = getAudioClip(url, name);
	if (clip != null) {
	    clip.play();
	}
    }

    /**
     * Initializes the applet.
     * You never need to call this directly, it is called automatically
     * by the system once the applet is created.
     * @see #start
     * @see #stop
     * @see #destroy
     */
    public void init() {
    }

    /**
     * Called to start the applet. You never need to call this method
     * directly, it is called when the applet's document is visited.
     * @see #init
     * @see #stop
     * @see #destroy
     */
    public void start() {
    }

    /**
     * Called to stop the applet. It is called when the applet's document is
     * no longer on the screen. It is guaranteed to be called before destroy()
     * is called. You never need to call this method directly.
     * @see #init
     * @see #start
     * @see #destroy
     */
    public void stop() {
    }

    /**
     * Cleans up whatever resources are being held. If the applet is active
     * it is stopped stopped.

     * @see #init
     * @see #start
     * @see #stop
     */
    public void destroy() {
    }
}
