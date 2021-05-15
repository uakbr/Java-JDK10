/*
 * @(#)LITagRef.java	1.8 95/03/14 Jonathan Payne
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

import java.util.*;
import java.io.*;
import awt.*;
import net.*;
import net.www.http.*;
import net.www.html.*;

/**
 * Class LITagRef is created for <li> tags that appear in html
 * document.
 * @version 1.8, 14 Mar 1995
 * @author Jonathan Payne
 */

public class LITagRef extends DisplayItemTagRef {
    public LITagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    /**
     * Builds the DisplayItem for this tag by asking the the list
     * context associated with the WRFormatter to create the next
     * tag.
     */
    public void buildDisplayItem(WRFormatter f) {
	WRListRef	r = f.listContext();

	if (r != null) {
	    di = r.getNextBullet(f);
	}
    }

    public void apply(WRFormatter f) {
	WRListRef	ref = f.listContext();
	int newMargin;

	f.breakLine(0);
	if (ref != null && !(ref instanceof DLTagRef)) {
	    f.setLeftMargin(ref.margin);
	    super.apply(f);	
	    newMargin = f.getXCoordinate() + 8;
	    f.setXCoordinate(newMargin);
	    f.setLeftMargin(newMargin);
	}
    }
}
