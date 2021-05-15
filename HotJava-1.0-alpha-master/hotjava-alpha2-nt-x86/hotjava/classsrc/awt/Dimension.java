/*
 * @(#)Dimension.java	1.6 95/01/31 Arthur van Hoff
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
 * A class to encapsulate objects that have a width and a height.
 * 
 * @version 1.6 31 Jan 1995
 * @author Arthur van Hoff, Sami Shaio
 */
public class Dimension {
    public int width;
    public int height;

    /** Constructors */
    public Dimension() {
    }

    public Dimension(Dimension d) {
	set(d.width, d.height);
    }
    
    public Dimension(int w, int h) {
	set(w, h);
    }

    /** Change it */
    public void set(int w, int h) {
	width = w;
	height = h;
    }

    /** Set "other" to be the max of this dimension with itself. */
    public Dimension max(Dimension other) {
	if (width > other.width)
	    other.width = width;
	if (height > other.height)
	    other.height = height;
	return other;
    }

    /** Add the width and height of "other" to this dimension. */
    public void add(Dimension other) {
	width += other.width;
	height += other.height;
    }

    public String toString() {
	return getClass().getName() + "[width=" + width + ", height=" + height + "]";
    }

    /**
     * If h is true then add value to the height otherwise add it to 
     * the width 
     */
    public void addDim(boolean h, int value) {
	if (h)
	    height += value;
	else
	    width += value;
    }

    /**
     * The same as addDim with an integer value but takes a Dimension.
     */
    public void addDim(boolean h, Dimension s) {
	if (h)
	    height += s.height;
	else
	    width += s.width;
    }

    /**
     * Clone this dimension.
     */
    public Object clone() {
	return super.clone();
    }
}
