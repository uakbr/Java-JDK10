/*
 * @(#)Style.java	1.7 95/03/14 Jonathan Payne
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
import net.www.html.*;
import awt.*;

/**
 * The Style class is used to specify most of the style changes
 * that occur as a result of html tag refs.  Zero or more of the
 * parameters may be specified for change.  BasicStyle operates on
 * instances of FormattingParameters, generating new instances of
 * FormattingParameters by modifying old ones.
 * @see awt.FormattingParameters
 * @see BasicStyle
 * @version 1.7, 14 Mar 1995
 * @author Jonathan Payne
 */

public class Style {
    public boolean renders = true;	/** is the text rendered? */

    public void start(WRFormatter f, TagRef ref) {
	if (!renders) {
	    f.stopRendering();
	}
    }

    public void finish(WRFormatter f, TagRef ref) {
	if (!renders) {
	    f.startRendering();
	}
    }

    public Style(String init) {
	if (init != null) {
	    StringTokenizer t = new StringTokenizer(init, ", ");

	    while (t.hasMoreTokens())
		handleStyleSpec(t.nextToken());
	}
    }

    protected String specAttribute(String spec) {
	int i;

	if ((i = spec.indexOf('=')) == -1) {
	    throw new Exception("Bad style spec: " + spec);
	}

	return spec.substring(0, i);
    }

    protected String specValue(String spec) {
	int i;

	if ((i = spec.indexOf('=')) == -1) {
	    throw new Exception("Bad style spec: " + spec);
	}

	return spec.substring(i + 1);
    }

    protected void handleStyleSpec(String spec) {
	if (specAttribute(spec).equals("renders")) {
	    renders = specValue(spec).equals("true");
	} else {
	    throw new Exception("Unknown style specification: " + spec);
	}
    }
}
