/*
 * @(#)ContainerLayout.java	1.7 95/01/31 Arthur van Hoff
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
 * A layout object (abstract class). There are serveral subclasses of
 * this class that layout containers in various ways. To layout a
 * container you need to call Layout(container). 
 *
 * @version 1.7 31 Jan 1995
 * @author Arthur van Hoff
 */
public class ContainerLayout {
    static final int NORTH = 0;
    static final int EAST  = 1;
    static final int SOUTH = 2;
    static final int WEST  = 3;

    /**
     * Return the preferred size of this layout given the size of
     * pTarget. 
     */
    public Dimension getPreferredSize(Container pTarget) {
	return new Dimension(pTarget.width, pTarget.height);
    }

    /**
     * Return the minimum dimensions of this layout.
     */
    public Dimension minDimension(Container pTarget) {
	return getPreferredSize(pTarget);
    }

    /**
     * Layout the target container.
     */
    public abstract void layout(Container pTarget);

    protected int getInsets(Container pTarget)[] {
	return pTarget.insets;
    }
}
