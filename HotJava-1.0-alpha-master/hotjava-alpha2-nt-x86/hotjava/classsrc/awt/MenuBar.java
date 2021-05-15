/*
 * @(#)MenuBar.java	1.19 95/02/03 Sami Shaio
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

import java.lang.*;
import java.util.*;

/**
 * A class that encapsulates the platform's concept of a menubar bound
 * to a Frame.
 *
 * @version 1.19 03 Feb 1995
 * @author Sami Shaio
 *
 */
public class MenuBar extends Component {
    Vector	pMenus;
    Font	defaultFont;

    /** Constructs a MenuBar for the given Frame f. */
    public MenuBar(Frame f) {
	super(f,"*Menu*");
	defaultFont = f.defaultFont;
	f.wServer.menuBarCreate(this, f);
	f.menuBar = this;
	pMenus = new Vector();
    }

    /** Destroys this menu bar. */
    public void dispose() {
	parent.wServer.menuBarDispose(this);
    }


    void   addMenu(Menu m) {
	pMenus.addElement(m);
    }

    int	nMenus() {
	return pMenus.size();
    }

    /**
     * Return the minimum size of the object
     */
    public Dimension minDimension() {
	// add up the widths of all the menus
	Dimension	d = new Dimension(0,0);

	d.height = 25;
	d.width = (pMenus.size() * 100);

	return d;
    }

    public Dimension getPreferredSize() {
	if (dim == null) {
	    dim = new Dimension(parent.width, 25);
	}
	
	return dim;
    }
}

