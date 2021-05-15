/*
 * @(#)Dimension.java	1.7 95/08/17 Arthur van Hoff
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

/**
 * A class to encapsulate a width and a height Dimension.
 * 
 * @version 	1.7, 08/17/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Dimension {
    
    /**
     * The width dimension.
     */
    public int width;

    /**
     * The height dimension.
     */
    public int height;

    /** 
     * Constructs a Dimension with a 0 width and 0 height.
     */
    public Dimension() {
	this(0, 0);
    }

    /** 
     * Constructs a Dimension and initializes it to the specified value.
     * @param d the specified dimension for the width and height values
     */
    public Dimension(Dimension d) {
	this(d.width, d.height);
    }

    /** 
     * Constructs a Dimension and initializes it to the specified width and
     * specified height.
     * @param width the specified width dimension
     * @param height the specified height dimension
     */
    public Dimension(int width, int height) {
	this.width = width;
	this.height = height;
    }

    /**
     * Returns the String representation of this Dimension's values.
     */
    public String toString() {
	return getClass().getName() + "[width=" + width + ",height=" + height + "]";
    }
}
