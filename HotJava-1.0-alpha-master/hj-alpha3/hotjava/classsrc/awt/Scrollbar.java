/*
 * @(#)Scrollbar.java	1.18 95/02/03 Sami Shaio
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

/**
 * A Class that represents a native scrollbar object.
 *
 * @version 1.18 03 Feb 1995
 * @author Sami Shaio
 */
public class Scrollbar extends Component {
    private Scrollbarable	scrollTarget;
    private WServer		wServer;
    public static final int	HORIZONTAL = 0;
    public static final int	VERTICAL = 1;

    /**
     * Constructs a scrollbar.
     * @param parent the parent window which will contain this scrollbar.
     * @param name the name of the scrollbar. May be significant
     * according to the layout method in parent.
     * @param orientation either Scrollbar.HORIZONTAL or Scrollbar.VERTICAL
     * @param manageScrollbar if true then parent will automatically
     * layout this scrollbar according to the platform's toolkit conventions.
     */
    public Scrollbar(Window parent,
		     String name,
		     int orientation,
		     boolean manageScrollbar) {
	super(parent, name, false);
	scrollTarget = parent;
	wServer = parent.wServer;
	wServer.scrollbarCreate(this, parent, orientation, manageScrollbar);
	if (manageScrollbar) {
	    parent.managed[orientation] = this;
	} else {
	    parent.addChild(this, name);
	}
    }

    /**
     * Sets the object which will handle callbacks for this scrollbar.
     * @see awt.Scrollbarable
     */
    public void setScrollTarget(Scrollbarable target) {
	scrollTarget = target;
    }

    /**
     * Sets the values for this scrollbar.
     * @param newValue is the position in the current window.
     * @param visible is the amount visible per page
     * @param minimum is the minimum value of the scrollbar
     * @param maximum is the maximum value of the scrollbar
     */
    public void setValues(int newValue, int visible, int minimum, int maximum) {
	wServer.scrollbarSetValues(this, newValue, visible, minimum, maximum);
    }

    /**
     * Shows this scrollbar.
     */
    public void map() {
	wServer.scrollbarShow(this);
    }

    /**
     * Hides this scrollbar.
     */
    public void unMap() {
	wServer.scrollbarHide(this);
    }

    /**
     * Returns the minimum value of this scrollbar.
     */
    public int minimum() {
	return wServer.scrollbarMinimum(this);
    }

    /**
     * Returns the maximum value of this scrollbar.
     */
    public int maximum() {
	return wServer.scrollbarMaximum(this);
    }

    /**
     * Returns the current value of this scrollbar.
     */
    public int value() {
	return wServer.scrollbarValue(this);
    }

    /**
     * Reshapes this scrollbar. This will not work if the scrollbar is
     * being managed by the window.
     * @see Scrollbar#Scrollbar
     */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.scrollbarReshape(this, x, y, w, h);
    }

    /**
     * Moves this scrollbar. This will not work if the scrollbar is
     * being managed by the window.
     */
    public void move(int x, int y) {
	wServer.scrollbarMoveTo(this, x, y);
    }

    /**
     * Disposes of this scrollbar. It should not be used afterwards.
     */
    public void dispose() {
	wServer.scrollbarDispose(this);
    }
}
