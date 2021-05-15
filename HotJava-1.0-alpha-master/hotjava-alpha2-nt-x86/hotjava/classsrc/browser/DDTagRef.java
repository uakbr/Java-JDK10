/*
 * @(#)DDTagRef.java	1.9 95/03/14 Jonathan Payne
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
 * An instance of class DDTagRef is created for <dd> tags that
 * appear in html document.
 * @version 1.9, 14 Mar 1995
 * @author Jonathan Payne
 */

public class DDTagRef extends WRTagRef {
    static int MIN_SPACING = 10;
    static int TERM_SPACE = 45;

    public DDTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void apply(WRFormatter f) {
	WRListRef   r = f.listContext();
	boolean	    compact = (r != null && r.getAttribute("compact") != null);
	int	    lm;

	if (r != null) {
	    lm = r.margin;
	} else {
	    lm = 20;
	}
	int ddstart = lm + TERM_SPACE;

	if (!compact || f.getXCoordinate() + MIN_SPACING >= ddstart) {
	    f.breakLine(0);
	} else {
	    f.setXCoordinate(ddstart);
	}
	f.setLeftMargin(ddstart);
    }
}
