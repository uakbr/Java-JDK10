/*
 * @(#)DisplayItem.java	1.13 95/03/14 Jonathan Payne
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

import browser.Observable;

/**
 * DisplayItem is an object that can be embedded inside a
 * DisplayItemWindow. It is essentially a virtual window inside another
 * window that allows painting and input handling.
 *
 * @see awt.DisplayItemWindow
 * @version 1.13 14 Mar 1995
 * @author Jonathan Payne
 */
public class DisplayItem extends Observable implements Layoutable {
    public DisplayItemWindow	parent;
    public int	    x;
    public int	    y;
    public int	    width;
    public int	    height;
    protected Color	    fgColor;
    /** Item is not valid if it has been resized. */
    protected boolean valid = false;

    public DisplayItem() {}

    public DisplayItem(int x, int y, int width, int height) {
	reshape(x, y, width, height);
    }

    public void setParent(DisplayItemWindow p) {
	parent = p;
    }

    public Dimension getPreferredSize() {
	return new Dimension(width, height);
    }

    public Dimension minDimension() {
	return getPreferredSize();
    }

    public void layout() {
    }

    public Color getFGColor() {
	return fgColor;
    }

    public String toString() {
	return getClass().getName() + "[" + x + ", " + y + ", " + width + ", " + height + "]";
    }

    public void move(int x, int y) {
	this.x = x;
	this.y = y;
//	if (parent != null)
//	    parent.childChanged(this);
    }

    public void reshape(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
//	if (parent != null)
//	    parent.childChanged(this);
	invalidate();
    }

    public Layoutable getChild(String name) {
	return null;
    }

    public void resize(int w, int h) {
	width = w;
	height = h;
	invalidate();
//	if (parent != null)
//	    parent.childChanged(this);
    }

    public void setColor(Color c) {
	fgColor = c;
	invalidate();
    }

    public void refresh() {
	if (parent != null)
	    parent.paint();
    }


    /** Return true if x,y are contained inside this DisplayItem. By
     * default, this method will test x,y against the bounding box
     * for this item.
     */
    public boolean containsPoint(int x, int y) {
	boolean in =  !(x < this.x || x > this.x + width ||
			y < this.y || y > this.y + height);
	return in;
    }

    /** Called when this item is no longer needed.  This is used,
	for example, to cancel backgroud tasks associated with
	animations */
    public void deactivate() {
	setParent(null);
    }

    public void invalidate() {
	valid = false;
    }

    public void validate() {
	valid = true;
    }

    public void paint(Window window, int x, int y) {
	if (!valid)
	    validate();
    }

    /** This is called when the display item needs updating.  The
	background is not cleared, so it is possible for the display
	item to do incremental updating with no flashing.  By default,
	this clears the background and calls paint. */
    public void update(Window window, int x, int y) {
	window.clearRect(x, y, width + 1, height + 1);
	paint(window, x, y);
    }

    public void requestUpdate() {
	if (parent != null) {
	    parent.paintChild(this, false);
	}
    }

    public void trackStart(Event e) {
    }

    public void trackMotion(Event e) {
    }

    public void trackStop(Event e) {
    }

    /** Enter with mouse button already down.  Only possible
	if sticky tracking is turned off. */
    public void trackEnter(Event e) {
    }

    /** Exit with mouse button already down.  Only possible
	if sticky tracking is turned off. */
    public void trackExit(Event e) {
    }

    // Keyboard input handling.  You have to register with
    //   your DisplayItemWindow's FocusManager before you'll
    //   ever see focus or keypress events!
    
    public void gotFocus() {
    }

    public void lostFocus() {
    }

    public void keyPressed(int key) {
    }
}
