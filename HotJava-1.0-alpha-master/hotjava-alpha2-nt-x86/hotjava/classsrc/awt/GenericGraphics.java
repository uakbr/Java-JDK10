/*-
 * Copyright (c) 1994 by Sun Microsystems, Inc.
 * All Rights Reserved.
 *
 * @(#)GenericGraphics.java	1.12 95/03/17 11/14/94
 *
 *      Sami Shaio, 11/14/94
 */
package awt;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * GenericGraphics is the base class for all graphics contexts for various
 * devices.  It will eventually be renamed to "Graphics".
 * 
 * @version 1.12 17 Mar 1995
 * @author Sami Shaio
 */
public class GenericGraphics {
    public Color	background;
    public Color	foreground;
    public Font		font = null;
    public int		originX;
    public int		originY;
    public float	scaleX;
    public float	scaleY;

    /**
     * Create a graphics context.
     */
    public GenericGraphics(int oX, int oY, float sX, float sY) { 
	originX = oX;
	originY = oY;
	scaleX = sX;
	scaleY = sY;
    }

    /**
     * Create a graphics context with origin at (0,0)
     */
    public GenericGraphics() {
	this(0, 0, 1.0, 1.0);
    }

    /**
     * Create a new Graphics Object based on this one.
     */
    public abstract GenericGraphics createChild(int oX, int oY,
						float sX, float sY);

    /**
     * Disposes of this Graphics context. It can't be used after being
     * disposed.
     */
    public void dispose() {
    }

    /**
     * Sets the font for all subsequent text-drawing operations.
     */
    public void setFont(Font f) {
	font = f;
    }

    /**
     * Sets the foreground color.
     */
    public void setForeground(Color c) {
	foreground = c;
    }

    /**
     * Sets the background color.
     */
    public void setBackground(Color c) {
	background = c;
    }

    /**
     * Paints a highlighted rectangle.
     */
    public void paint3DRect(int x, int y, int w, int h,
			    boolean fill, boolean raised) {
	if (fill) {
	    if (raised) {
		setBackground(Color.menuBright);
	    }
	    fillRect(x, y, w, h);
	}
	setForeground(raised ? Color.menuBright : Color.menuDim);
	drawLine(x, y, x, y + h - 1);				// left
	drawLine(x + 1, y, x + w - 2, y);			// top
	setForeground(raised ? Color.menuDim : Color.menuBright);
	drawLine(x + 1, y + h - 1, x + w - 1, y + h - 1);	// bottom
	drawLine(x + w - 1, y, x + w - 1, y + h - 1);		// right

	setForeground(Color.black);
    }    

    /** Sets the clipping rectangle for this Graphics context. */
    public abstract void clipRect(int X, int Y, int W, int H);

    /** Clears the clipping region. */
    public abstract void clearClip();
    
    /** Clears the rectangle indicated by x,y,w,h. */
    public abstract void clearRect(int X, int Y, int W, int H);
    /** Fills the given rectangle with the foreground color. */
    public abstract void fillRect(int X, int Y, int W, int H);
    /** Draws the given rectangle. */
    public abstract void drawRect(int X, int Y, int W, int H);
    /** Draws the given string. */
    public abstract void drawString(String str, int x, int y);
    /** Draws the given character array. */
    public abstract void drawChars(char chars[], int offset, int length,
				   int x, int y);
    /** Draws the given string and returns the length of the drawn
      string in pixels.  If font isn't set then returns -1. */
    public abstract int drawStringWidth(String str, int x, int y);
    /** Draws the given character array and return the width in
      pixels. If font isn't set then returns -1. */
    public abstract int drawCharsWidth(char chars[], int offset, int length,
				       int x, int y);
    /** Draws the given line. */
    public abstract void drawLine(int x1, int y1, int x2, int y2);

    /** Draws an image at x,y. */
    public abstract void drawImage(Image I, int X, int Y);

    /**
     * Copies an area of the window that this graphics context paints to.
     * @param X the x-coordinate of the source.
     * @param Y the y-coordinate of the source.
     * @param W the width.
     * @param H the height.
     * @param dx the x-coordinate of the destination.
     * @param dy the y-coordinate of the destination.
     */
    public abstract void copyArea(int X, int Y, int W, int H, int dx, int dy);

    /**
     * Sets the origin of this Graphics context. All subsequent
     * operations are relative to this origin.
     */
    public void setOrigin(int x, int y) {
	originX = x;
	originY = y;
    }

    /**
     * Sets the scaling factor for this Graphics context. Currently
     * only used for line and rectangle drawing operations.
     */
    public void setScaling(float sx, float sy) {
	scaleX = sx;
	scaleY = sy;
    }
}
