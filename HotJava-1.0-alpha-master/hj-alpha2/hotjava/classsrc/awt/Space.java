/*
 * @(#)Space.java	1.3 95/02/03 Sami Shaio
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
package awt;

/**
 * Space is a component that has no visible representation. It is
 * useful for adding extra space to a layout in selected places.
 *
 * @version 1.3 03 Feb 1995
 * @author Sami Shaio
 */
public class Space extends Component {
    boolean hFill;
    boolean vFill;
    Dimension pSize;

    public Space(Container parent,
		 String name,
		 int width,
		 int height,
		 boolean hFill,
		 boolean vFill) {
	super(parent, name);
	this.width = width;
	this.height = height;
	this.hFill = hFill;
	this.vFill = vFill;
    }

    /** Return the preferred size of this space object. */
    public Dimension getPreferredSize() {
	if (!hFill && !vFill) {
	    if (pSize == null) {
		pSize = new Dimension(width, height);
	    }
	    return pSize;
	} else {
	    return new Dimension((hFill) ? parent.width : width,
				 (vFill) ? parent.height : height);
	}
    }
}
