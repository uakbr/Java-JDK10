/*
 * @(#)AnchorTagRef.java	1.8 95/03/14 Jonathan Payne
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
 * An instance of class AnchorTagRef is created for each occurrence
 * of <a ...> tags in an html document.  An AnchorTagRef is either
 * an anchor (i.e., it points to another document) or it's an endpoint
 * for some other document (i.e., it has no "href" attribute).
 * @version 1.8, 14 Mar 1995
 * @author Jonathan Payne
 */

public class AnchorTagRef extends StyleTagRef {
    final int	UNKNOWN = 0;
    final int	ISANCHOR = 1;
    final int	NOTANCHOR = 2;

    int	anchorState = UNKNOWN;

    public AnchorTagRef(Tag t, int pos, boolean isEnd, Style s) {
	super(t, pos, isEnd, s);
    }

    public void apply(WRFormatter f) {
	if (!isEnd) {
	    if (anchorState == UNKNOWN) {
		if (getAttribute("href") != null) {
		    anchorState = ISANCHOR;
		} else {
		    anchorState = NOTANCHOR;
		}
	    }
	}
	super.apply(f);
    }

    /**
     * Returns true if this anchor contains a link to another
     * document.
     */
    public boolean isLink() {
	return anchorState == ISANCHOR;
    }
}
