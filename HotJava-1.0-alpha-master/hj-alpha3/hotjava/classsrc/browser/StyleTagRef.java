/*
 * @(#)StyleTagRef.java	1.7 95/03/14 Jonathan Payne
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
 * Class StyleTagRef is used for html tags that have Style's
 * associated with them.  This handles the apply method by turning
 * it into start/finish methods on the associated style.
 * @version 1.7, 14 Mar 1995
 * @author Jonathan Payne
 */

public class StyleTagRef extends WRTagRef {
    Style   style;

    public StyleTagRef(Tag t, int pos, boolean isEnd, Style s) {
	super(t, pos, isEnd);
	style = s;
    }

    public void apply(WRFormatter f) {
	if (style != null) {
	    if (!isEnd) {
		style.start(f, this);
	    } else {
		style.finish(f, this);
	    }
	}
    }
}
