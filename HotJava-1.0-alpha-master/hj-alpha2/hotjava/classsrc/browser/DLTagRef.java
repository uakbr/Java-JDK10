/*
 * @(#)DLTagRef.java	1.8 95/03/14 Jonathan Payne
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
 * An instance of class DLTagRef is created for <dl> tags that
 * appear in html document.
 * @version 1.8, 14 Mar 1995
 * @author Jonathan Payne
 */

public class DLTagRef extends WRListRef {
    public DLTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    static int	MARGIN_INCREMENT = 50;

    public DisplayItem getNextBullet(WRFormatter f) {
	return null;
    }

    public void apply(WRFormatter f) {
	if (!isEnd) {
	    WRListRef   r = f.listContext();

	    /* I am just trying to imitate Mosaic here ... I don't
	       understand it, I just make it work ... JP */
	    if (r == null) {
		margin = f.getLeftMargin();
		f.breakLine(12);
	    } else {
		margin = r.margin + MARGIN_INCREMENT;
	    }
	    f.pushParameters(null);
	    f.setLeftMargin(margin);
	    f.pushList(this);
	} else {
	    f.popList();
	    if (f.listContext() == null) {
		f.breakLine(12);
	    }
	    f.popParameters();
	}
    }
}					    
