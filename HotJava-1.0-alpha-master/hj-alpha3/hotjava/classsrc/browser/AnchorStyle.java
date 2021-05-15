/*
 * @(#)AnchorStyle.java	1.7 95/03/14 Jonathan Payne
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

import java.io.*;
import awt.*;
import java.util.*;
import net.www.html.*;

/**
 * Class AnchorStyle is a BasicStyle that pushes an anchor onto the
 * anchor stack associated with the formatter.  This doesn't apply
 * the BasicStyle if this anchor is not a link.
 * @version 1.7, 14 Mar 1995
 * @author Jonathan Payne
 */

public class AnchorStyle extends BasicStyle {
    public AnchorStyle(String spec) {
	super(spec);
    }

    public void start(WRFormatter f, TagRef ref) {
	if (((AnchorTagRef) ref).isLink()) {
	    super.start(f, ref);
	}
	f.pushAnchor(ref);
    }

    public void finish(WRFormatter f, TagRef ref) {
	AnchorTagRef	atr = (AnchorTagRef) f.popAnchor();

	if (atr != null && atr.isLink()) {
	    super.finish(f, ref);
	}
    }
}
