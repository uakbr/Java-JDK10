/*
 * @(#)WRTagRef.java	1.8 95/03/20 Jonathan Payne
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

import awt.*;
import net.www.html.TagRef;
import net.www.html.Tag;

/**
 * Class WRTagRef is the superclass for all hotjava tag refs that
 * are properly handled by instances of WRFormatter.  Whenever a
 * WRFormatter encounters a tag reference in the document it's
 * laying out, the apply method is called.
 * @see WRFormatter
 * @see awt.Formatter
 * @see Document
 * @version 1.8, 20 Mar 1995
 * @author Jonathan Payne
 */

public class WRTagRef extends TagRef {
    public abstract void apply(WRFormatter f);

    public WRTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }
}
