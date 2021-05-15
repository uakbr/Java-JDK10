/*-
 * Copyright (c) 1994 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)Graphics.java	1.6 95/02/23 11/14/94
 *
 *      Sami Shaio, 11/14/94
 */
package awt;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Graphics is an object that encapsulates a graphics context for a
 * particular window.  It will eventually be renamed "WSGraphics".
 * For now, there is an empty "WSGraphics" subclass which is identical
 * to this class.
 * 
 * @version 1.6 23 Feb 1995
 * @author Sami Shaio
 */

public class Graphics extends GenericGraphics {
    int			pData;

    public Window	drawSurface;
    public WServer	wServer;

    /**
     * Create a graphics context.
     */
    public Graphics(Window w, int oX, int oY, float sX, float sY) { 
	super(oX, oY, sX, sY);

	wServer = w.wServer;
	drawSurface = w;
	wServer.graphicsCreate(this, w);
    }

    /**
     * Create a graphics context with origin at (0,0)
     */
    public Graphics(Window w) {
	this(w, 0, 0, 1.0, 1.0);
    }

    protected Graphics() {
    }

    /**
     * Create a new Graphics Object based on this one.
     */
    public Graphics createChild(int oX, int oY, float sX, float sY) {
	return (Graphics) new Graphics(drawSurface, oX, oY, sX, sY);
    }

    /**
     * Disposes of this Graphics context. It can't be used after being
     * disposed.
     */
    public void dispose() {
	wServer.graphicsDispose(this);
    }

    /**
     * Sets the font for all subsequent text-drawing operations.
     */
    public void setFont(Font f) {
	super.setFont(f);
	wServer.graphicsSetFont(this, f);
    }

    /**
     * Sets the foreground color.
     */
    public void setForeground(Color c) {
	super.setForeground(c);
	wServer.graphicsSetForeground(this, c);
    }


    /**
     * Sets the background color.
     */
    public void setBackground(Color c) {
	super.setBackground(c);
	wServer.graphicsSetBackground(this, c);
    }

    /**
     * Paints a highlighted rectangle.
     */
    public void paint3DRect(int x, int y, int w, int h,
			    boolean fill, boolean raised) {
	// Dummy routine so that awt/Graphics:paint3DRect() method resolves.
	// Required for Alpha 1.0 backwards compatibility of Graphics class.
	super.paint3DRect(x, y, w, h, fill, raised);
    }
    /** Sets the clipping rectangle for this Graphics context. */
    public void clipRect(int X, int Y, int W, int H) {
	wServer.graphicsClipRect(this, X, Y, W, H);
    }

    /** Clears the clipping region. */
    public void clearClip() {
	wServer.graphicsClearClip(this);
    }
    
    /** Clears the rectangle indicated by x,y,w,h. */
    public void clearRect(int X, int Y, int W, int H) {
	wServer.graphicsClearRect(this, X, Y, W, H);
    }
    /** Fills the given rectangle with the foreground color. */
    public void fillRect(int X, int Y, int W, int H) {
	wServer.graphicsFillRect(this, X, Y, W, H);
    }
    /** Draws the given rectangle. */
    public void drawRect(int X, int Y, int W, int H) {
	wServer.graphicsDrawRect(this, X, Y, W, H);
    }
    /** Draws the given string. */
    public void drawString(String str, int x, int y) {
	wServer.graphicsDrawString(this, str, x, y);
    }
    /** Draws the given character array. */
    public void drawChars(char chars[], int offset, int length, int x, int y) {
	wServer.graphicsDrawChars(this, chars, offset, length, x, y);
    }
    /** Draws the given byte array. */
    public void drawBytes(byte bytes[], int offset, int length, int x, int y) {
	wServer.graphicsDrawBytes(this, bytes, offset, length, x, y);
    }
    /** Draws the given string and returns the length of the drawn
      string in pixels.  If font isn't set then returns -1. */
    public int drawStringWidth(String str, int x, int y) {
	return wServer.graphicsDrawStringWidth(this, str, x, y);
    }
    /** Draws the given character array and return the width in
      pixels. If font isn't set then returns -1. */
    public int drawCharsWidth(char chars[], int offset, int length, int x, int y) {
	return wServer.graphicsDrawCharsWidth(this, chars, offset, length, x, y);
    }
    /** Draws the given character array and return the width in
      pixels. If font isn't set then returns -1. */
    public int drawBytesWidth(byte bytes[], int offset, int length, int x, int y) {
	return wServer.graphicsDrawBytesWidth(this, bytes, offset, length, x, y);
    }
    /** Draws the given line. */
    public void drawLine(int x1, int y1, int x2, int y2) {
	wServer.graphicsDrawLine(this, x1, y1, x2, y2);
    }

    /** Draws an image at x,y. */
    public void drawImage(Image I, int X, int Y) {
    	wServer.graphicsDrawImage(this, I, X, Y);
    }

    /**
     * Copies an area of the window that this graphics context paints to.
     * @param X the x-coordinate of the source.
     * @param Y the y-coordinate of the source.
     * @param W the width.
     * @param H the height.
     * @param dx the x-coordinate of the destination.
     * @param dy the y-coordinate of the destination.
     */
    public void copyArea(int X, int Y, int W, int H, int dx, int dy) {
	wServer.graphicsCopyArea(this, X, Y, W, H, dx, dy);
    }

    /**
     * Sets the origin of this Graphics context. All subsequent
     * operations are relative to this origin.
     */
    public void setOrigin(int x, int y) {
	super.setOrigin(x, y);
	wServer.graphicsSetOrigin(this, x, y);
    }

    /**
     * Sets the scaling factor for this Graphics context. Currently
     * only used for line and rectangle drawing operations.
     */
    public void setScaling(float sx, float sy) {
	super.setScaling(sx, sy);
	wServer.graphicsSetScaling(this, scaleX, scaleY);
    }
}

class WSGraphics extends Graphics {
    public WSGraphics(Window w, int oX, int oY, float sX, float sY) { 
	super(w, oX, oY, sX, sY);
    }
    public WSGraphics(Window w) {
	super(w);
    }
}
