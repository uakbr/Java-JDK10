/*
 * @(#)DisplayItemTagRef.java	1.10 95/03/20 Jonathan Payne
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

import net.www.html.Tag;
import awt.*;

/**
 * Class DisplayItemTagRef is the superclass of all hotjava tag
 * refs which result in the creation of display items.  Subclassers
 * must override the buildDisplayItem method, and often that's all
 * they need to override.
 * @see ImgTagRef
 * @see HRTagRef
 * @see Document
 * @version 1.10, 20 Mar 1995
 * @author Jonathan Payne
 */

public class DisplayItemTagRef extends WRTagRef {
    DisplayItem	di;

    public DisplayItemTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    abstract void buildDisplayItem(WRFormatter f);

    public void apply(WRFormatter f) {
	if (di == null) {
	    buildDisplayItem(f);
	}
	if (di != null) {
	    f.addDisplayItem(di, true);
	    f.addCharacterSpacing(' ');
	}
    }
}
