/*
 * @(#)HRTagRef.java	1.10 95/03/14 Jonathan Payne
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
 * Class HRTagRef is created for <hr> tags that appear in html
 * document.
 * @version 1.10, 14 Mar 1995
 * @author Jonathan Payne
 */

public class HRTagRef extends DisplayItemTagRef {
    static final int	breakHeight = 10;

    public HRTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void buildDisplayItem(WRFormatter f) {
	di = new HRDisplayItem(0);   /* we'll fill in width below */
    }

    public void apply(WRFormatter f) {
	if (di == null) {
	    buildDisplayItem(f);
	}
	String	widthSpec = getAttribute("width");
	int	width = -1;

	f.breakLine(breakHeight);

	if (widthSpec != null) {
	    int	c;
	    int	len = widthSpec.length();
	    boolean usePercent = false;

	    if (len > 0 && widthSpec.charAt(len - 1) == '%') {
		usePercent = true;
		widthSpec = widthSpec.substring(0, len - 1);
	    }
	    try {
		width = Integer.parseInt(widthSpec);
		if (usePercent) {
		    width = (f.win.width * width) / 100;
		}
	    } catch (Exception e) {
	    }
	}
	if (width <= 0) {
	    width = f.win.width - 2 * f.getLeftMargin();
	}

	String	align = getAttribute("align");
	if (align != null) {
	    if (align.equals("left")) {
		/* remind - how do I know 20? */
		f.setXCoordinate(20);
	    } else if (align.equals("center")) {
		f.setXCoordinate((f.win.width - width) / 2);
	    } else if (align.equals("right")) {
		f.setXCoordinate(f.win.width - width - 20);
	    }
	}

	String	sizeString = getAttribute("size");
	int	size = 2;

	if (sizeString != null) {
	    try {
		size = Integer.parseInt(sizeString);
	    } catch (Exception e) {
	    }
	}

	if (getAttribute("noshade") != null) {
	    ((HRDisplayItem) di).shade = false;
	}

	di.resize(width, size);

	super.apply(f);
	f.breakLine(breakHeight);
    }
}
