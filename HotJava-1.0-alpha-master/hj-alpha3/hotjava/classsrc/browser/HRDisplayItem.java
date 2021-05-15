/*
 * @(#)HRDisplayItem.java	1.10 95/03/14 Jonathan Payne
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
 * Class HRDisplayItem is used to display html horizontal rulers
 * <hr> that appear in html documents.
 * @version 1.10, 14 Mar 1995
 * @author Jonathan Payne
 */

public class HRDisplayItem extends DisplayItem {
    boolean shade = true;

    public HRDisplayItem(int width) {
	resize(width, 2);
    }

    public void paint(Window w, int x, int y) {
	if (shade) {
	    w.paint3DRect(x, y, width, height, false, false);
	} else {
	    w.setForeground(Color.menuDim);
	    w.fillRect(x, y, width, height);
	}
    }
}
