/*
 * @(#)WRListRef.java	1.9 95/03/14 Jonathan Payne
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
 * Class WRListRef is the super class of all the html tag refs that
 * exhibit list characteristics, such as dl, ol, ul.
 * @version 1.9, 14 Mar 1995
 * @author Jonathan Payne
 */

public class WRListRef extends WRTagRef {
    public WRListRef(Tag t, int pos, boolean isEnd, int margin) {
	super(t, pos, isEnd);
	marginIncrement = margin;
    }

    public WRListRef(Tag t, int pos, boolean isEnd) {
	this(t, pos, isEnd, 35);
    }

    protected int   marginIncrement;
    protected int   margin;

    public DisplayItem getNextBullet(WRFormatter f) {
	return null;
    }

    public void apply(WRFormatter f) {
	if (!isEnd) {
	    f.pushParameters(null);
	    margin = f.getLeftMargin() + marginIncrement;
	    f.setLeftMargin(margin);
	    if (f.listContext() == null) {
		f.breakLine(12);
	    }
	    f.pushList(this);
	} else {
	    f.popList();
	    if (f.listContext() == null) {
		f.breakLine(12);
	    }
	    f.popParameters();
	}
    }
}					    
