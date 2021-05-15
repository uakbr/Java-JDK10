/*
 * @(#)AppTagRef.java	1.6 95/03/14 Jonathan Payne, Arthur van Hoff
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

package browser;

import net.www.html.*;

/**
 * This class represents the "app" tag.
 * @version 1.6, 14 Mar 1995
 * @author Jonathan Payne
 */
public
class AppTagRef extends WRTagRef {
    AppletDisplayItem	item;

    public AppTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void apply(WRFormatter f) {
	if (item == null) {
	    item = new AppletDisplayItem(f.win, ((WRWindow)f.win).document().url(), this);
	}
	if (item != null) {
	    f.addDisplayItem(item, true);
	    f.addCharacterSpacing(' ');
	    ((WRWindow)f.win).document().addApplet(item);
	}
    }
}
