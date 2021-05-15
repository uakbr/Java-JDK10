/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextEditLine.java	1.5 94/05/24 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

import awt.Graphics;
import awt.Color;

public class TextEditLine extends TextViewLine {
    boolean selected;
    int selx0, selx1;

    TextEditLine(TextEditor pOwner, int pos, int viewwidth) {
	super(pOwner, pos, viewwidth);

	int selStart = pOwner.selStart;
	int selEnd = pOwner.selEnd;

	if (selStart == selEnd) {
	    if ((selStart >= start) && (selStart < end)) {
		selx0 = posToX(selStart) + 1;
		selected = true;
	    }
	} else if ((start <= selEnd) && (end > selStart)) {
	    selx0 = (selStart < start) ? 1 : posToX(selStart) + 1;
	    selx1 = (selEnd >= end) ? viewwidth : posToX(selEnd) + 1;
	    selected = true;
	}
    }

    protected void paint(Graphics g, int dx, int dy) {
	if (selected) {
	    int selStart = ((TextEditor)owner).selStart;
	    int selEnd = ((TextEditor)owner).selEnd;

	    if (selStart == selEnd) {
		g.setForeground(Color.black);
		g.drawLine(selx0, y, selx0, y + height - 1);
		g.drawLine(selx0 - 2, y + height - 1,
			   selx0 + 2, y + height - 1);
	    } else {
		((TextEditor)owner).paintSelect(g, selx0, y, selx1 - selx0, height);
	    }
	}
	super.paint(g, dx, dy);
    }
}
