/*
 * @(#)Scrollbar.java	1.20 95/12/14 Sami Shaio
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
package java.awt;

import java.awt.peer.ScrollbarPeer;

/**
 * A Scrollbar component.
 *
 * @version	1.20 12/14/95
 * @author 	Sami Shaio
 */
public class Scrollbar extends Component {
  
    /**
     * The horizontal Scrollbar variable.
     */
    public static final int	HORIZONTAL = 0;

    /**
     * The vertical Scrollbar variable.
     */
    public static final int	VERTICAL   = 1;

    /**
     * The value of the Scrollbar.
     */
    int	value;

    /**
     * The maximum value of the Scrollbar.
     */
    int	maximum;

    /**
     * The minimum value of the Scrollbar.
     */
    int	minimum;

    /**
     * The size of the visible portion of the Scrollbar.
     */
    int	sVisible;

    /**
     * The Scrollbar's orientation--being either horizontal or vertical.
     */
    int	orientation;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a line.
     */
    int lineIncrement = 1;

    /**
     * The amount by which the scrollbar value will change when going
     * up or down by a page.
     */
    int pageIncrement = 10;

    
    /**
     * Constructs a new vertical Scrollbar.
     */
    public Scrollbar() {
	this(VERTICAL);
    }

    /**
     * Constructs a new Scrollbar with the specified orientation.
     * @param orientation either Scrollbar.HORIZONTAL or Scrollbar.VERTICAL
     * @exception IllegalArgumentException When an illegal scrollbar orientation is given.
     */
    public Scrollbar(int orientation) {
	switch (orientation) {
	  case Scrollbar.HORIZONTAL:
	  case Scrollbar.VERTICAL:
	    this.orientation = orientation;
	    break;

	  default:
	    throw new IllegalArgumentException("illegal scrollbar orientation");
	}
    }

    /**
     * Constructs a new Scrollbar with the specified orientation,
     * value, page size,  and minumum and maximum values.
     * @param orientation either Scrollbar.HORIZONTAL or Scrollbar.VERTICAL
     * @param value the scrollbar's value
     * @param visible the size of the visible portion of the
     * scrollable area. The scrollbar will use this value when paging up
     * or down by a page.
     * @param minimum the minimum value of the scrollbar
     * @param maximum the maximum value of the scrollbar
     */
    public Scrollbar(int orientation, int value, int visible, int minimum, int maximum) {
	this(orientation);
	setValues(value, visible, minimum, maximum);
    }

    /**
     * Creates the Scrollbar's peer.  The peer allows you to modify
     * the appearance of the Scrollbar without changing any of its
     * functionality.
     */

    public synchronized void addNotify() {
	peer = getToolkit().createScrollbar(this);
	super.addNotify();
    }

    /**
     * Returns the orientation for this Scrollbar.
     */
    public int getOrientation() {
	return orientation;
    }

    /**
     * Returns the current value of this Scrollbar.
     * @see #getMinimum
     * @see #getMaximum
     */
    public int getValue() {
	return value;
    }

    /**
     * Sets the value of this Scrollbar to the specified value.
     * @param value the new value of the Scrollbar. If this value is
     * below the current minimum or above the current maximum, it becomes the
     * new one of those values, respectively.
     * @see #getValue
     */
    public void setValue(int value) {
	if (value < minimum) {
	    value = minimum;
	}
	if (value > maximum) {
	    value = maximum;
	}
	if (value != this.value) {
	    this.value = value;
	    ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	    if (peer != null) {
		peer.setValue(value);
	    }
	}
    }

    /**
     * Returns the minimum value of this Scrollbar.
     * @see #getMaximum
     * @see #getValue
     */
    public int getMinimum() {
	return minimum;
    }

    /**
     * Returns the maximum value of this Scrollbar.
     * @see #getMinimum
     * @see #getValue
     */
    public int getMaximum() {
	return maximum;
    }

    /**
     * Returns the visible amount of the Scrollbar.
     */
    public int getVisible() {
	return sVisible;
    }

    /**
     * Sets the line increment for this scrollbar. This is the value
     * that will be added (subtracted) when the user hits the line down
     * (up) gadgets.
     */
    public void setLineIncrement(int l) {
	lineIncrement = l;
	if (peer != null) {
	    ((ScrollbarPeer)peer).setLineIncrement(l);
	}
    }

    /**
     * Gets the line increment for this scrollbar.
     */
    public int getLineIncrement() {
	return lineIncrement;
    }

    /**
     * Sets the page increment for this scrollbar. This is the value
     * that will be added (subtracted) when the user hits the page down
     * (up) gadgets.
     */
    public void setPageIncrement(int l) {
	pageIncrement = l;
	if (peer != null) {
	    ((ScrollbarPeer)peer).setPageIncrement(l);
	}
    }

    /**
     * Gets the page increment for this scrollbar.
     */
    public int getPageIncrement() {
	return pageIncrement;
    }

    /**
     * Sets the values for this Scrollbar.
     * @param value is the position in the current window.
     * @param visible is the amount visible per page
     * @param minimum is the minimum value of the scrollbar
     * @param maximum is the maximum value of the scrollbar
     */
    public void setValues(int value, int visible, int minimum, int maximum) {
	if (maximum < minimum) {
	    maximum = minimum;
	}
	if (value < minimum) {
	    value = minimum;
	}
	if (value > maximum) {
	    value = maximum;
	}

	this.value = value;
	this.sVisible = visible;
	this.minimum = minimum;
	this.maximum = maximum;
	ScrollbarPeer peer = (ScrollbarPeer)this.peer;
	if (peer != null) {
	    peer.setValues(value, sVisible, minimum, maximum);
	}
    }

    /**
     * Returns the String parameters for this Scrollbar.
     */
    protected String paramString() {
	return super.paramString() +
	    ",val=" + value +
	    ",vis=" + visible +
	    ",min=" + minimum +
	    ",max=" + maximum +
	    ((orientation == VERTICAL) ? ",vert" : ",horz");
    }
}
