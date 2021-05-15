/*
 * @(#)Frame.java	1.38 95/12/01 Sami Shaio
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

import java.awt.peer.FramePeer;

/**
 * A Frame is a top-level window with a title.
 * The default layout for a frame is BorderLayout.
 *
 * @version 	1.38, 12/01/95
 * @author 	Sami Shaio
 */
public class Frame extends Window implements MenuContainer {
    public static final int	DEFAULT_CURSOR   		= 0;
    public static final int	CROSSHAIR_CURSOR 		= 1;
    public static final int	TEXT_CURSOR 	 		= 2;
    public static final int	WAIT_CURSOR	 		= 3;
    public static final int	SW_RESIZE_CURSOR	 	= 4;
    public static final int	SE_RESIZE_CURSOR	 	= 5;
    public static final int	NW_RESIZE_CURSOR		= 6;
    public static final int	NE_RESIZE_CURSOR	 	= 7;
    public static final int	N_RESIZE_CURSOR 		= 8;
    public static final int	S_RESIZE_CURSOR 		= 9;
    public static final int	W_RESIZE_CURSOR	 		= 10;
    public static final int	E_RESIZE_CURSOR			= 11;
    public static final int	HAND_CURSOR			= 12;
    public static final int	MOVE_CURSOR			= 13;	
    
    String 	title = "Untitled";
    Image  	icon;
    MenuBar	menuBar;
    boolean	resizable = true;
    Image	cursorImage;
    int		cursorType = DEFAULT_CURSOR;
    Color	cursorFg;
    Color	cursorBg;
    
    /**
     * Constructs a new Frame that is initially invisible.
     * @see Component#resize
     * @see Component#show
     */
    public Frame() {
	visible = false;
	setLayout(new BorderLayout());
    }
    
    /**
     * Constructs a new, initially invisible Frame with the specified 
     * title.
     * @param title te specified title 
     * @see Component#resize
     * @see Component#show
     */
    public Frame(String title) {
	this();
	this.title = title;
    }
    
    /**
     * Creates the Frame's peer.  The peer allows us to change the look
     * of the Frame without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createFrame(this);
	if (menuBar != null) {
	    menuBar.addNotify();
	    ((FramePeer)peer).setMenuBar(menuBar);
	}
	super.addNotify();
    }
    
    /**
     * Gets the title of the Frame.
     * @see #setTitle
     */
    public String getTitle() {
	return title;
    }
    
    /**
     * Sets the title for this Frame to the specified title.
     * @param title the specified title of this Frame
     * @see #getTitle
     */
    public void setTitle(String title) {
	this.title = title;
	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setTitle(title);
	}
    }
    
    /**
     * Returns the icon image for this Frame.
     */
    public Image getIconImage() {
	return icon;
    }
    
    /**
     * Sets the image to display when this Frame is iconized. Note that
     * not all platforms support the concept of iconizing a window.
     * @param image the icon image to be displayed
     */
    public void setIconImage(Image image) {
	this.icon = image;
	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setIconImage(image);
	}
    }
    
    /**
     * Gets the menu bar for this Frame.
     */
    public MenuBar getMenuBar() {
	return menuBar;
    }
    
    /**
     * Sets the menubar for this Frame to the specified menubar.
     * @param mb the menubar being set
     */
    public synchronized void setMenuBar(MenuBar mb) {
	if (menuBar == mb) {
	    return;
	}
	if (mb.parent != null) {
	    mb.parent.remove(mb);
	}
	if (menuBar != null) {
	    remove(menuBar);
	}
	menuBar = mb;
	menuBar.parent = this;
	
	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    menuBar.addNotify();
	    peer.setMenuBar(menuBar);
	}
    }
    
    /**
     * Removes the specified menu bar from this Frame.
     */
    public synchronized void remove(MenuComponent m) {
	if (m == menuBar) {
	    FramePeer peer = (FramePeer)this.peer;
	    if (peer != null) {
		menuBar.removeNotify();
		menuBar.parent = null;
		peer.setMenuBar(null);
	    }
	    menuBar = null;
	}
    }
    
    /**
     * Disposes of the Frame. This method must
     * be called to release the resources that
     * are used for the frame.
     */
    public synchronized void dispose() {
	if (menuBar != null) {
	    remove(menuBar);
            menuBar = null;
	}
        super.dispose();
    }
    
    /**
     * Returns true if the user can resize the Frame.
     */
    public boolean isResizable() {
	return resizable;
    }
    
    /**
     * Sets the resizable flag.
     * @param resizable true if resizable; false otherwise.
     */
    public void setResizable(boolean resizable) {
	this.resizable = resizable;
	FramePeer peer = (FramePeer)this.peer;
	if (peer != null) {
	    peer.setResizable(resizable);
	}
    }
    
    /**
     * Set the cursor image to a predefined cursor.
     * @param cursorType one of the cursor constants defined above.
     */
    public void setCursor(int cursorType) {
	if (cursorType < DEFAULT_CURSOR || cursorType > MOVE_CURSOR) {
	    throw new IllegalArgumentException("illegal cursor type");
	}
	this.cursorType = cursorType;
	if (peer != null) {
	    ((FramePeer)peer).setCursor(cursorType);
	}
    }

    /**
     * Return the cursor type
     */
    public int getCursorType() {
	return cursorType;
    }
    
    /**
     * Returns the parameter String of this Frame.
     */
    protected String paramString() {
	String str = super.paramString();
	if (resizable) {
	    str += ",resizable";
	}
	if (title != null) {
	    str += ",title=" + title;
	}
	return str;
    }
}
