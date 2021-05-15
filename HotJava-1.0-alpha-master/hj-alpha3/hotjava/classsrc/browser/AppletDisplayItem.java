/*
 * @(#)AppletDisplayItem.java	1.26 95/05/11 Arthur van Hoff
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
import net.www.html.*;
import java.io.InputStream;

/**
 * Class AppletDisplayItem is created as a place holder for all
 * applets that appear in hotjava-aware html documents.
 *
 * @version 1.26, 11 May 1995
 * @author Arthur van Hoff
 */

public
class AppletDisplayItem extends DisplayItem implements Alignable {
    static public final int CREATED	= 0;
    static public final int LOADING	= 1;
    static public final int LOADED	= 2;
    static public final int INITIALIZING= 3;
    static public final int STARTED	= 4;
    static public final int STOPPED	= 5;
    static public final int DESTROYED	= 6;
    static public final int ERROR	= 7;

    /** Thickness of the load-status icon border. */
    static public final int BORDER = 2;

    /** Total thickness of the icon border. */
    static final int FULL_BORDER = BORDER * 2;    

    /** Size of status icons + border. */
    static public final int DEFAULT_SIZE= 38 + FULL_BORDER;

    /**
     * "Applet Delayed" icon.
     */
    static public Image appletDelayedImage = null;

    /**
     * "Applet Failed" icon.
     */
    static public Image appletFailedImage = null;

    /**
     * "Applet Loading" icon.
     */
    static public Image appletLoadingImage = null;

    /**
     * True, once an attempt has been made to load
     * the applet.
     */
    private int status;
    
    /**
     * The applet displayed in this item.
     */
    private Applet applet;

    /**
     * The message that is displayed after an error or
     * during loading...
     */
    private String msg;

    /**
     * The applet alignment
     */
    private int align;

    /**
     * The document URL.
     */
    private URL documentURL;

    /**
     * The applet URL.
     */
    private URL appletURL;

    /**
     * The Tag.
     */
    private TagRef tag;

    /**
     * The class name.
     */
    private String className;

    /**
     * Get the status of the applet.
     */
    public final int getStatus() {
	return status;
    }

    /**
     * Get alignment for formatting.
     */
    public int getAlign() {
	return align;
    }

    static private Image getLoadIcon(Window w, String name) {
	URL imgURL = new URL(null, name);
	InputStream iStream = imgURL.openStream();
	GifImage gif = new GifImage(iStream, null);
	return w.createImage(gif);
    }
    
    /**
     * Initialize the load status icons.
     */
    static private void maybeInitLoadIcons(Window w) {
	if (appletDelayedImage == null) {
	    final String base = "doc:demo/images/";
	    
	    appletDelayedImage = getLoadIcon(w, base+"applet-delayed.gif");
	    appletFailedImage = getLoadIcon(w, base+"applet-failed.gif");
	    appletLoadingImage = getLoadIcon(w, base+"applet-loading.gif");
	}
    }
    
    /**
     * Creates an applet display item, if possible use the width/height
     * attributes to resize to the proper initial size.
     */
    public AppletDisplayItem(DisplayItemWindow parent, URL documentURL, TagRef tag) {
	this.documentURL = documentURL;
	this.tag = tag;
	
	int w = DEFAULT_SIZE;
	int h = DEFAULT_SIZE;

	align = WRImageItem.convertAlign(tag.getAttribute("align"));

	String	attr;
	if ((attr = tag.getAttribute("width")) != null) {
	    try {
		w = Integer.parseInt(attr);
	    } catch (NumberFormatException ee) {
	    }
	}
	if ((attr = tag.getAttribute("height")) != null) {
	    try {
		h = Integer.parseInt(attr);
	    } catch (NumberFormatException ee) {
	    }
	}
	resize(w, h);

	// Get the class name
	className = tag.getAttribute("class");

	// Determine the applet URL
	String src = tag.getAttribute("src");
	appletURL = (src != null) ? new URL(documentURL, src) : documentURL;

	if (className != null) {
	    msg = "Applet " + className + " from " + appletURL.toExternalForm();
	} else {
	    status = ERROR;
	    className = "<no name>";
	    msg = "No class name for this applet.";
	}

	if (!WRWindow.delayAppletLoading) {
	    setParent(parent);
	    load();
	    init();
	    setParent(null);
	}

	maybeInitLoadIcons(parent);
    }

    public void requestUpdate() {
	if (status == STARTED) {
	    if (parent != null) {
		parent.paintChild(this, false);
	    }
	}
    }

    /**
     * Load the applet
     */
    public void load() {
	// Don't bother if the applet was already loaded.
	if (status == CREATED) {
	    status = LOADING;

	    // Try loading the applet locally
	    try {
		applet = (Applet)appletURL.New(className);
	    } catch (Exception ex) {
		msg = "Applet " + className + " from " + appletURL.toExternalForm() + " not loaded.";
		status = ERROR;
		System.err.println(msg);
	    }
	    if (applet != null) {
		applet.item = this;
		applet.documentURL = documentURL;
		applet.appletURL = appletURL;
		applet.tag = tag;	
		status = LOADED;
	    }
	}
    }

    /**
     * Initialize the applet. This should be called after
     * the parent of the display item is set. The font will
     * be the default font of the applet.
     */
    public void init() {
	if (status == LOADED) {
	    status = INITIALIZING;
	    applet.width = width;
	    applet.height = height;
	    applet.font = parent.wServer.fonts.getFont("TimesRoman", Font.PLAIN, 14);
	    parent.getFontMetrics(applet.font);
	    applet.fgColor = Color.black;
	    applet.bgColor = parent.background;

	    try {
		applet.init();
		status = STOPPED;
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to intialize: " + ex;
		System.err.println(msg);
		status = ERROR;
	    } catch (Object o) {
		msg = "Failed to intialize: " + o;
		System.err.println(msg);
		status = ERROR;
	    }
	}
    }

    /**
     * Start the applet
     */
    public void start() {
	if (status == STOPPED) {
	    status = STARTED;
	    try {
		applet.start();
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		System.err.println(msg);
		msg = "Failed to start: " + ex;
	    } catch (Object o) {
		msg = "Failed to start: " + o;
		System.err.println(msg);
	    }


	}
    }

    /**
     * Stop the applet
     */
    public void stop() {
	if (status == STARTED) {
	    try {
		applet.stop();
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to stop: " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to stop: " + o;
		System.err.println(msg);
	    }
	    status = STOPPED;
	}
    }

    /**
     * Destroy the applet
     */
    public void destroy() {
	if (status != STOPPED) {
	    stop();
	}
	if (status == STOPPED) {
	    try {
		applet.destroy();
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to stop: " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to stop: " + o;
		System.err.println(msg);
	    }
	    applet = null;
	    status = DESTROYED;
	}
    }

    /**
     * Paint the applet, after damage.
     */
    public void paint(Window w, int x, int y) {
	Graphics g = w.graphics.createChild(x, y, 1, 1);
	try {
	    paint(g);
	} finally {
	    g.dispose();
	}
    }

    /**
     * Paint the applet, after damage.
     */
    public void update(Window w, int x, int y) {
	Graphics g = w.graphics.createChild(x, y, 1, 1);
	try {
	    update(g);
	} finally {
	    g.dispose();
	}
    }

    private void safeDrawImage(Graphics g, Image i, int x, int y,
			       int inWidth, int inHeight) {
	if (i != null && (i.width / 2) < inWidth && (i.height / 2) < inHeight) {
	    g.clipRect(BORDER, BORDER, inWidth, inHeight);
	    g.drawImage(i, x, y);
	}
    }

    /**	
     * Paint the applet, given a graphics context.
     */
    public void paint(Graphics g) {
	g.clipRect(0, 0, width, height);
	switch (status) {
	  case STOPPED:
	  case STARTED:
	    g.setFont(applet.font);
	    g.setForeground(applet.fgColor);
	    try {
		applet.paint(g);
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to paint: " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to paint: " + o;
		System.err.println(msg);
	    }
	    break;
	  case LOADING:
	    g.setForeground(Color.yellow);
	    g.paint3DRect(0, 0, width, height, true, true);
	    safeDrawImage(g, appletLoadingImage, BORDER, BORDER,
			  width - FULL_BORDER, height - FULL_BORDER);
	    break;
	  case ERROR:
	    g.setForeground(Color.red);
	    g.paint3DRect(0, 0, width, height, true, true);
	    safeDrawImage(g, appletFailedImage, BORDER, BORDER,
			  width - FULL_BORDER, height - FULL_BORDER);
	    break;
	  default:
	    if (((WRWindow)parent).delayAppletLoading) {
		g.setForeground(Color.yellow);
		g.paint3DRect(0, 0, width, height, true, true);
		safeDrawImage(g, appletDelayedImage, BORDER, BORDER,
			      width - FULL_BORDER, height - FULL_BORDER);
	    } else {
		g.paint3DRect(0, 0, width, height, false, true);
		safeDrawImage(g, appletLoadingImage, BORDER, BORDER,
			      width - FULL_BORDER, height - FULL_BORDER);
	    }
	    break;
	}
    }

    /**	
     * Paint the applet, given a graphics context.
     */
    public void update(Graphics g) {
	g.clipRect(0, 0, width, height);
	switch (status) {
	  case STOPPED:
	  case STARTED:
	    g.setFont(applet.font);
	    g.setForeground(applet.fgColor);
	    try {
		applet.update(g);
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to update: " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to update: " + o;
		System.err.println(msg);
	    }
	    break;
	  case LOADING:
	    g.setForeground(Color.yellow);
	    g.paint3DRect(0, 0, width, height, true, true);
	    safeDrawImage(g, appletLoadingImage, BORDER, BORDER,
			  width - FULL_BORDER, height - FULL_BORDER);
	    break;
	  case ERROR:
	    g.setForeground(Color.red);
	    g.paint3DRect(0, 0, width, height, true, true);
	    safeDrawImage(g, appletFailedImage, BORDER, BORDER,
			  width - FULL_BORDER, height - FULL_BORDER);
	    break;
	  default:
	    if (((WRWindow)parent).delayAppletLoading) {
		g.setForeground(Color.yellow);
		g.paint3DRect(0, 0, width, height, true, true);
		safeDrawImage(g, appletDelayedImage, BORDER, BORDER,
			      width - FULL_BORDER, height - FULL_BORDER);
	    } else {
		g.paint3DRect(0, 0, width, height, false, true);
		safeDrawImage(g, appletLoadingImage, BORDER, BORDER,
			      width - FULL_BORDER, height - FULL_BORDER);
	    }
	    break;
	}
    }

    /**
     * Mouse down
     */
    public void trackStart(Event e) {
	if (status == STARTED) {
	    try {
		applet.mouseDown(e.x, e.y);
	    } catch (ThreadDeath td) {
		throw td;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to trackStart: " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to trackStart: " + o;
		System.err.println(msg);
	    }
	}
    }

    /**
     * Mouse move
     */
    public void trackMotion(Event e) {
	if (status == STARTED) {
	    try {
		if (e.id == Event.MOUSE_MOTION) {
		    applet.mouseMove(e.x, e.y);
		} else {
		    applet.mouseDrag(e.x, e.y);
		}
	    } catch (ThreadDeath td) {
		throw td;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to mouse"
		      + (e.id == Event.MOUSE_MOTION ? "Move " : "Drag ") 
		      + ex; 
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to mouse"
		      + (e.id == Event.MOUSE_MOTION ? "Move " : "Drag ")
		      + o;
		System.err.println(msg);
	    }
	}
    }

    /**
     * Mouse up
     */
    public void trackStop(Event e) {
	if (status == STARTED) {
	    try {
		applet.mouseUp(e.x, e.y);
	    } catch (ThreadDeath td) {
		throw td;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to trackStop " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to trackStop " + o;
		System.err.println(msg);
	    }
	} else if ((status == CREATED) && ((WRWindow)parent).delayAppletLoading) {
	    ((WRWindow)parent).status("Loading applet " + className + " from " + documentURL.toExternalForm() + "...");
	    load();
	    init();
	    ((WRWindow)parent).relayout();
	    start();
	    ((WRWindow)parent).status("Applet " + className + " loaded!");
	}
    }

    /**
     * Mouse enter
     */
    public void trackEnter(Event e) {
	if (status == STARTED) {
	    try {
		applet.mouseEnter();
	    } catch (ThreadDeath td) {
		throw td;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to trackEnter " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to trackEnter " + o;
		System.err.println(msg);
	    }
	} else if (msg != null) {
	    ((WRWindow)parent).status(msg);
	}
    }

    /**
     * Mouse exit
     */
    public void trackExit(Event e) {
	if (status == STARTED) {
	    try {
		applet.mouseExit();
	    } catch (ThreadDeath td) {
		throw td;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to trackExit " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to trackExit " + o;
		System.err.println(msg);
	    }
	} else if (msg != null) {
	    ((WRWindow)parent).status("");
	}
    }

    /**
     * Got focus
     */
    public void gotFocus() {
	if (status == STARTED) {
	    try {
		applet.gotFocus();
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to gotFocus " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to gotFocus " + o;
		System.err.println(msg);
	    }
	}
    }

    /**
     * Lost focus
     */
    public void lostFocus() {
	if (status == STARTED) {
	    try {
		applet.lostFocus();
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to lostFocus " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to lostFocus " + o;
		System.err.println(msg);
	    }
	}
    }

    /**
     * KeyPressed
     */
    public void keyPressed(int key) {
	if (status == STARTED) {
	    try {
		applet.keyDown(key);
	    } catch (ThreadDeath e) {
		throw e;
	    } catch (Exception ex) {
		ex.printStackTrace();
		msg = "Failed to keyDown " + ex;
		System.err.println(msg);
	    } catch (Object o) {
		msg = "Failed to dkeyDowngotFocus " + o;
		System.err.println(msg);
	    }
	}
    }
}
