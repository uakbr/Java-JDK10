/*
 * @(#)BreakingStyle.java	1.10 95/03/14 Jonathan Payne
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
 * Class BreakingStyle is a BasicStyle which also allows for
 * paragraph breaks.  This style is associated with such tags as
 * &lt;p>, &lt;h1>, &lt;li>, and so on.  A break size may be
 * specified in the constructor.
 * @version 1.10, 14 Mar 1995
 * @author Jonathan Payne
 */

public class BreakingStyle extends BasicStyle {
    int	amount;

    public BreakingStyle(String spec) {
	super((amount = -1, spec));
    }

    protected void handleStyleSpec(String spec) {
	String attr = specAttribute(spec);

	if (attr.equals("break")) {
	    setBreak(Integer.parseInt(specValue(spec)));
	} else {
	    super.handleStyleSpec(spec);
	}
    }

    void doBreak(WRFormatter f) {
	int amt = (amount == -1) ? 16 : amount;

	f.breakLine(amt);
    }

    public void start(WRFormatter f, TagRef ref) {
	doBreak(f);
	super.start(f, ref);
    }

    public void finish(WRFormatter f, TagRef ref) {
	doBreak(f);
	super.finish(f, ref);
    }

    void setBreak(int amount) {
	this.amount = amount;
    }
}
