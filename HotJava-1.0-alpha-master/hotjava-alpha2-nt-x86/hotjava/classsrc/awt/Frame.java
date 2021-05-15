/*
 * @(#)Frame.java	1.39 95/03/20 Sami Shaio
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

import awt.*;
import java.util.*;

/**
 * A Frame is a top-level window that can contain other windows.
 *
 * @version 1.39 20 Mar 1995
 * @author Sami Shaio
 */
public class Frame extends Container {
    static final Vector	allFrames = new Vector();
    MenuBar	menuBar;
    boolean placed = false;
    public Font defaultFont = null;
    public Color background;
    private static Frame defaultParent;	// this is a very unfortunate hack

    /* Cache of the value returned from WServer.frameHasStatusBar(). */
    boolean hasNativeStatusBar;    

    /* If !hasNativeStatusBar, the frame creates a status bar 
       from a label.  Used only when !hasNativeStatusBar. */
    Label statusBar;

    /* Current status message */
    public String statusMessage;

    /**
     * Constructs a Frame.
     * @param ws is the WServer to create this Frame.
     * @param hasTitleBar is true if this Frame should have a title region.
     * @param isModal specifies whether the Frame is modal or not. If
     * it is modal, then Frame.map will block until the frame is unmapped.
     * @param parentFrame if not null then this Frame will be attached
     * to the given Frame and serve the function of a dialog window.
     * @param w is the width of this Frame.
     * @param h is the height of this Frame.
     * @param bg is the background color of this Frame.
     */
    public Frame(WServer ws,
		 boolean hasTitleBar,
		 boolean isModal,
		 Frame parentFrame,
		 int w,
		 int h,
		 Color bg) {
	super(null, null);
	if (defaultParent == null)
	    defaultParent = this;
	wServer = ws;
	background = bg;
	wServer.frameCreate(this, hasTitleBar, isModal, parentFrame, w, h, bg);
	setLayout(BorderLayout.defaultLayout);
	addInsets(0, 0, 0, 0);
        hasNativeStatusBar = wServer.frameHasStatusBar(this);
	allFrames.addElement(this);
    }

    /**
     * Creates a modeless Frame.
     */
    public Frame(WServer ws,
		 boolean hasTitleBar,
		 Frame parentFrame,
		 int w,
		 int h,
		 Color bg) {
	this(ws, hasTitleBar, false, parentFrame, w, h, bg);
    }

    /**
     * Constructs a Frame.  Unlike the previous constructor, this does not
     * have a "parent" parameter.  It uses a default parent parameter that
     * is the first Frame ever created.  In general, this is a <i>bad</i>
     * constructor to call, but unfortunatly there are occasionally situations
     * where Frames need to be created, but finding an appropriate parent is
     * impossible.
     * @param hasTitleBar is true if this Frame should have a title region.
     * @param w is the width of this Frame.
     * @param h is the height of this Frame.
     * @param bg is the background color of this Frame.
     */
    public Frame(boolean hasTitleBar,
		 int w,
		 int h,
		 Color bg) {
	this(defaultParent.wServer, hasTitleBar, defaultParent, w, h, bg);
//	if (defaultParent == null)
//	    throw new IllegalArgumentException("No default parent frame");
    }


    /**
     * Sets the default font to use for all gui elements contained in
     * this Frame.
     */
    public synchronized void setDefaultFont(Font f) {
	defaultFont = f;
	wServer.frameSetDefaultFont(this, defaultFont);
    }

    /**
     * Called to inform the Frame that its size has changed and it
     * should layout its children.
     */
    public synchronized void resize() {
	tidy = false;
	if (mapped) {
	    layout();
	    tidy = true;
	}
    }

    /**
     * Returns the preferred dimension of this frame.
     */
    public Dimension getPreferredSize() {
	return (theLayout != null) ?
	    theLayout.getPreferredSize(this) :
	    super.getPreferredSize();
    }

    /**
     * Returns the minimum dimension to hold this Frame.
     */
    public Dimension minDimension() {
	Dimension	m;

	if (theLayout != null) {
	    m = theLayout.minDimension(this);
	    return m;
	} else {
	    return super.minDimension();
	}
    }

    /** Shows this Frame.*/
    public void map() {
	super.map();
	wServer.frameShow(this);
	if (!placed) {
	    resize();
	    wServer.sync();
	    placed = true;
	}
    }

    /** Hides this Frame. */
    public void unMap() {
	wServer.frameHide(this);
	super.unMap();
    }

    /** Sets the title of this frame. */
    public void setTitle(String title) {
	wServer.frameSetTitle(this, title);
    }

    /** Disposes of this Frame. */
    public void dispose() {
	int size = allFrames.size();
	wServer.frameDispose(this);
	for (int i = 0; i < size; i++) {
	    if (allFrames.elementAt(i) == this) {
		allFrames.removeElementAt(i);
		break;
	    }
	}
    }

    /** Override this method to take some action when the user decides
     * to destroy this Frame.
     */
    public void handleQuit() {
	System.exit(0);
    }


    /** Moves this Frame. UNIMPLEMENTED */
    public void   move(int newX, int newY) {
    }

    /**
     * Set the minimum size of the window
     */
    public void setMinSize(int pWidth, int pHeight) {
	wServer.frameSetMinSize(this, pWidth, pHeight);
    }

    /** Sets the minimum size that this frame can be resized to and
     * still show all its children.
     */
    public void setMinSize() {
	Dimension d = minDimension();
	setMinSize(d.width, d.height);
	if (!mapped) {
	    resize(d.width, d.height);
	}
    }

    /**
     * Reshapes this Frame to the given dimensions.
     */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	if (mapped) {
	    unMap();
	    wServer.frameReshape(this, x, y, w, h);
	    layout();
	    map();
	} else {
	    wServer.frameReshape(this, x, y, w, h);
	    layout();
	}
    }

    /*<not public yet>
     * Shows or hides the status bar. 
     * @param show is true if the status bar should be displayed.
     */
    public void showStatusBar(boolean show) {
        if (hasNativeStatusBar) {
            wServer.frameShowStatusBar(this, show);
        } else {
            if (show) {
		statusBar.map();
            } else {
		statusBar.unMap();
            }
        }
    }

    /*<not public yet>
     * Returns whether the status bar is visible or not.
     */
    public boolean showingStatusBar() {
        if (hasNativeStatusBar) {
            return wServer.frameShowingStatusBar(this);
        } else {
            return statusBar.mapped;
        }
    }

    /*<not public yet>
     * Displays the specified message in the status bar.
     * @param message is the message to display in the status bar.
     */
    public void setStatusMessage(String message) {
        statusMessage = message;
        if (hasNativeStatusBar) {
	    wServer.frameSetStatusMessage(this, message);
        } else {
            statusBar.setText(message);
        }
    }

    /**
     * Set the image to display when this Frame is iconized. Note that
     * not all platforms support the concept of iconizing a window.
     */
    public void setIconImage(Image image) {
	wServer.frameSetIconImage(this, image);
    }

   /* temporary. remove once status bar code is moved here and
      hotjava.java is updated appropriately. */
    public boolean hasStatusBar() {
	return wServer.frameHasStatusBar(this);
    }
}
