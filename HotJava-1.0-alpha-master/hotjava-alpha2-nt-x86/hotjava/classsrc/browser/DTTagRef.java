/*
 * @(#)DTTagRef.java	1.7 95/03/14 Jonathan Payne
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
 * Class DTTagRef is created for <dt> tags that appear in html
 * document.
 * @version 1.7, 14 Mar 1995
 * @author Jonathan Payne
 */

public class DTTagRef extends WRTagRef {
    public DTTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void apply(WRFormatter f) {
	WRListRef	ref = f.listContext();
	int		lm = -1;

	if (ref != null) {
	    lm = ref.margin;
	}
	f.breakLine(0);
	if (lm != -1) {
	    f.setLeftMargin(lm);
	}
    }
}
