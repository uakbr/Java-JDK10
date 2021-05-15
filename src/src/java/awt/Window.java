/*
 * @(#)Window.java	1.16 95/12/20 Arthur van Hoff
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
package java.awt;

import java.awt.peer.WindowPeer;

/**
 * A Window is a top-level window with no borders and no
 * menubar. It could be used to implement a pop-up menu.
 * The default layout for a window is BorderLayout.
 *
 * @version 	1.16, 12/20/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Window extends Container {
    String      warningString;

    Window() {
	SecurityManager sm = System.getSecurityManager();
	if ((sm != null) && !sm.checkTopLevelWindow(this)) {
	    warningString = System.getProperty("awt.appletWarning",
					       "Warning: Applet Window");
	}
    }

    /**
     * Constructs a new Window initialized to an invisible state. It
     * behaves as a modal dialog in that it will block input to other
     * windows when shown.
     *
     * @param parent the owner of the dialog
     * @see Component#resize
     * @see #show
     */
    public Window(Frame parent) {
	SecurityManager sm = System.getSecurityManager();

	if ((sm != null) && !sm.checkTopLevelWindow(this)) {
	    warningString = System.getProperty("awt.appletWarning",
					       "Warning: Applet Window");
	}
	this.parent = parent;
	visible = false;
	setLayout(new BorderLayout());
    }

    /**
     * Creates the Window's peer.  The peer allows us to modify the appearance of the
     * Window without changing its functionality.
     */
    public synchronized void addNotify() {
	if (peer == null) {
	    peer = getToolkit().createWindow(this);
	}
	super.addNotify();
    }

    /**
     * Packs the components of the Window.
     */
    public synchronized void pack() {
	if (peer == null) {
	    addNotify();
	}
	resize(preferredSize());
	validate();
    }

    /**
     * Shows the Window. This will bring the window to the
     * front if the window is already visible.
     * @see Component#hide
     */
    public synchronized void show() {
	if (peer == null) {
	    addNotify();
	}
	validate();

	if (visible) {
	    toFront();
	} else {
	    super.show();
	}
    }

    /**
     * Disposes of the Window. This method must
     * be called to release the resources that
     * are used for the window.
     */
    public synchronized void dispose() {
	hide();
	removeNotify();
    }

    /**
     * Brings the frame to the front of the Window.
     */
    public void toFront() {
	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toFront();
	}
    }

    /**
     * Sends the frame to the back of the Window.
     */
    public void toBack() {
	WindowPeer peer = (WindowPeer)this.peer;
	if (peer != null) {
	    peer.toBack();
	}
    }

    /**
     * Returns the toolkit of this frame.
     * @see Toolkit
     */
    public Toolkit getToolkit() {
	return Toolkit.getDefaultToolkit();
    }

    /**
     * Gets the warning string for this window. This is
     * a string that will be displayed somewhere in the
     * visible area of windows that are not secure.
     */
    public final String getWarningString() {
	return warningString;
    }
}
