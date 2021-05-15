/*
 * @(#)Window.java	1.66 95/02/23 Sami Shaio
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

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Window is a general purpose canvas that can contain other windows.
 *
 * @version 1.66 23 Feb 1995
 * @author Sami Shaio
 */
public class Window extends Container implements Scrollbarable {
    Scrollbar	managed[] = new Scrollbar[2];
    Vector	childWindows;
    public String title;

    static Window	getWindow(Container c) {
	while (! (c instanceof Window)) {
	    c = c.parent;
	}
	return (Window)c;
    }

    /** Background color for this window */
    public Color background;

    /** Foreground color for this window */
    public Color foreground;

    /** Default graphics object for this window. */
    public Graphics	graphics;

    /* Special WSGraphics object for some awt_WServer native methods. */
    private WSGraphics	wsgraphics;

    Window() {
	super(null,null);
    }

    /**
     * Create a window that is a child of another window.
     */
    public Window(Container w, String name,
		  Color bg, int wd, int ht) {
	super(w, name);
	background = bg;
	foreground = Color.black;
	if (w instanceof Frame) {
	    wServer = ((Frame)w).wServer;
	    wServer.windowCreateInFrame(this, (Frame)w,
					false, background, wd, ht);
	} else {
	    Window win = Window.getWindow(w);
	    wServer = win.wServer;
	    wServer.windowCreate(this, win, false, background, wd, ht);
	}
	childWindows = new Vector(5, 10);
	setLayout(FlowLayout.defaultLayout);
	wsgraphics = new WSGraphics(this);
	graphics = (Graphics) wsgraphics;
    }

    /** Backward compatibility with alpha1. Takes a Window as a parent
    	 instead of a container. */
    public Window(Window w, String name,
		  Color bg, int wd, int ht) {
	this((Container)w, name, bg, wd, ht);
    }

   /** Backward compatibility with alpha1. Takes a Frame as a parent
    	 instead of a container. */
    public Window(Frame w, String name,
		  Color bg, int wd, int ht) {
	this((Container)w, name, bg, wd, ht);
    }
 
    /** Disposes of this window and all the components it contains. */
    public void dispose() {
	if (managed[Scrollbar.VERTICAL] != null) {
	    managed[Scrollbar.VERTICAL].dispose();
	}
	if (managed[Scrollbar.HORIZONTAL] != null) {
	    managed[Scrollbar.HORIZONTAL].dispose();
	}
	graphics.dispose();
	if (graphics != wsgraphics)
	    wsgraphics.dispose();
	wServer.windowDispose(this);
	super.dispose();
    }

    /** Shows this window. */
    public void map() {
	wServer.windowShow(this);
	super.map();
    }

    /** Hides this window. */
    public void unMap() {
	wServer.windowHide(this);
	super.unMap();
    }

    /** Override this method to repaint the window when needed. */
    public void paint() {
    }

    /** Override this method to repaint only a portion of the window. */
    public void expose(int x, int y, int w, int h) {
	paint();
    }

    
    public void gotFocus() {
    }

    
    public void lostFocus() {
    }


    /** Callback for exposing a region of a window. Unless, you don't
     * want this callback to set the clipping region you should only
     * override expose, not handleExpose.
     * @see Window#expose
     */
    public synchronized void handleExpose(int x, int y, int w, int h) {
	// Should this use the wsgraphics object?
	graphics.clipRect(x, y, w, h);
	expose(x, y, w, h);
	graphics.clearClip();
    }

    /** Callback to override to take some action when the window is
     * resized.
     */
    public void handleResize() {
    }

    /** Returns the menubar object useable for this window. */
    public MenuBar getMenuBar() {
	return getFrame().menuBar;
    }


    /** Returns the top-level window that contains this window. */
    public Frame getFrame() {
	Component c;
	Window w = this;

	for (;;) {
	    c = w.parent;
	    if (c instanceof Frame) {
		return (Frame)c;
	    }
	}
    }

    public void handleMouseDown(int x, int y, int modifiers) {
	mouseDown(new Event(Event.MOUSE_DOWN,
			    this,
			    x, y,
			    false, 0, modifiers));
    }
    public void handleMouseUp(int x, int y, int modifiers) {
	mouseUp(new Event(Event.MOUSE_UP,
			  this,
			  x, y,
			  false, 0, modifiers));
    }
    public void handleMouseDrag(int x, int y, int modifiers) {
	mouseDrag(new Event(Event.MOUSE_DRAG,
			    this,
			    x, y,
			    false, 0, modifiers));
    }
    public void handleMouseMoved(int x, int y, int modifiers) {
	mouseMoved(new Event(Event.MOUSE_MOTION,
			     this,
			     x, y,
			     false, 0, modifiers));
    }
    public void handleMouseEnter(int x, int y) {
	mouseEnter(new Event(Event.MOUSE_ENTER,
			     this,
			     x, y,
			     false, 0));
    }
    public void handleMouseLeave(int x, int y) {
	mouseLeave(new Event(Event.MOUSE_LEAVE,
			     this,
			     x, y,
			     false, 0));
    }

    public void handleKeyPress(int k, int isAscii, int modifiers) {
	handleKeyPress((char)k, (isAscii == 0) ? false : true, modifiers);
    }

    public void handleKeyPress(char k, boolean isAscii, int modifiers) {
	keyPressed(new Event(Event.KEY_PRESS,
			     this,
			     -1,-1,
			     isAscii, k, modifiers));
    }

    /** Override this method to take some action when the mouse is
     * dragged with a button down over the window.
     */
    public void mouseDrag(Event e) {
	parent.handleEvent(e);
    }
    /**
     * Override this method to take some action when the mouse is
     * moved over the window without any buttons pressed. This will only
     * be activated if enablePointerMotionEvents has been called on this
     * window.
     */
    public void mouseMoved(Event e) {
	parent.handleEvent(e);
    }

    /** Override this method to take some action when the mouse is
     * pressed over this window.
     */
    public void mouseDown(Event e) {
	parent.handleEvent(e);
    }

    /** Override this method to take some action when the mouse button
     * is released.
     */
    public void mouseUp(Event e) {
	parent.handleEvent(e);
    }

    /** Override this method to take some action when the mouse enters
     * this window.
     */
    public void mouseEnter(Event e) {
	parent.handleEvent(e);
    }

    /**
     * Override this method to take some action when the mouse leaves
     * this window.
     */
    public void mouseLeave(Event e) {
	parent.handleEvent(e);
    }

    /** Override this method to take some action when a key is pressed
     * in this window.
     */
    public void keyPressed(Event e) {
	parent.handleEvent(e);
    }

    /** Moves this window. */
    public void move(int px, int py) {
	x = px;
	y = py;
	wServer.windowMoveTo(this, x, y);
    }

    /** Reshapes this window. */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.windowReshape(this, x, y, w, h);
	layout();
    }

    /** Enables notification of mouse motion without any buttons presed.*/
    public void enablePointerMotionEvents() {
	wServer.windowEnablePointerMotionEvents(this);
    }
    /** Disables notification of mouse motion without any buttons presed.*/
    public void disablePointerMotionEvents() {
	wServer.windowDisablePointerMotionEvents(this);
    }

    /** Sets the thickness of the margin in this window. */
    public void setMargin(int margin) {
	wServer.windowSetMargin(this, margin);
    }

    //
    // Scrolling methods
    //

    public void lineUp() {
    }
    public void lineDown() {
    }
    public void pageUp() {
    }
    public void pageDown() {
    }
    public void dragAbsolute(int value) {
    }
    public boolean scrollVertically(int dy) {
	return false;
    }
    public boolean scrollHorizontally(int dx) {
	return false;
    }

    public synchronized void scrollWindow(int dx, int dy) {
	wServer.windowScrollWindow(this, dx, dy);
    }

    public void copyArea(int X, int Y, int W, int H, int dx, int dy) {
	graphics.copyArea(X, Y, W, H, dx, dy);
    }

    public void drawImage(Image I, int X, int Y) {
    	graphics.drawImage(I, X, Y);
    }

    public Image createImage(DIBitmap dib) {
	if (dib == null)
	    return null;
	Image	im = new Image(this);
	wServer.imageCreate(im, dib);

	return im;
    }

    public Image createImage(DIBitmap dib, int w, int h) {
    	if (dib == null)
	    return null;
	Image im = new Image(this);
	if (w <= 0)
	    w = dib.width;
	if (h <= 0)
	    h = dib.height;
	if (w == dib.width && h == dib.height)
	    wServer.imageCreate(im, dib);
	else
	    wServer.scaledImageCreate(im, dib, 0, 0, dib.width, dib.height,
				      w, h);

	return im;
    }

    public DIBitmap retrieveDIBitmap(Image im) {
	if (im == null)
	    return null;
	DIBitmap dib = new DIBitmap(im.width, im.height);
	wServer.bitmapRetrieve(im, dib);

	return dib;
    }

    public void disposeImage(Image i) {
	wServer.imageDispose(i);
    }

    public void setFont(Font f) {
	graphics.setFont(f);
    }

    public void setForeground(Color c) {
	foreground = c;
	graphics.setForeground(c);
    }

    public void setBackground(Color c) {
	background = c;
	graphics.setBackground(c);
    }

    public void clipRect(int X, int Y, int W, int H) {
	graphics.clipRect(X, Y, W, H);
    }

    public void clearClip() {
	graphics.clearClip();
    }
    
    public void clearRect(int X, int Y, int W, int H) {
	graphics.clearRect(X, Y, W, H);
    }
    public void fillRect(int X, int Y, int W, int H) {
	graphics.fillRect(X, Y, W, H);
    }
    public void drawRect(int X, int Y, int W, int H) {
	graphics.drawRect(X, Y, W, H);
    }
    public void drawString(String str, int x, int y) {
	graphics.drawString(str, x, y);
    }
    public void drawChars(char buf[], int offset, int length, int x, int y) {
	graphics.drawChars(buf, offset, length, x, y);
    }
    public void drawBytes(byte buf[], int offset, int length, int x, int y) {
	graphics.drawBytes(buf, offset, length, x, y);
    }

    /** @see GenericGraphics#drawLine */
    public void drawLine(int x1, int y1, int x2, int y2) {
	graphics.drawLine(x1, y1, x2, y2);
    }
    
    /** @see GenericGraphics#paint3DRect */
    public void paint3DRect(int x, int y, int w, int h,
			    boolean fill, boolean raised) {
	graphics.paint3DRect(x, y, w, h, fill, raised);
    }

    public void update() {
	wServer.sync();
    }
}
