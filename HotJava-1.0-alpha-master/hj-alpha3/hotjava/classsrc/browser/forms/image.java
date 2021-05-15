/*
 * @(#)image.java	1.6 95/03/19 Jonathan Payne
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

package browser.forms;

import awt.WRImageItem;
import browser.WRFormatter;
import browser.WRWindow;
import awt.DisplayItem;
import awt.Event;

/**
 * An instance of class image is created for each occurrence of a
 * <input type=image> in an html form.  This exhibits image map
 * behavior.
 * @version 1.6, 19 Mar 1995
 * @author Jonathan Payne
 */
public class image extends FormItem {
    public DisplayItem buildDisplayItem(WRFormatter f) {
	return new FormImageItem((WRWindow) f.win, this);
    }

    Event   event;

    public String getFormString() {
	Event	e = event;

	event = null;
	if (e != null) {
	    return name + ".x=" + e.x + "&" + name + ".y=" + e.y;
	} else {
	    return null;
	}
    }

    public void Execute(Event e) {
	event = e;
	form.submit();
    }

    public void prime(WRFormatter f) {
	FormImageItem fim = (FormImageItem) displayItem;
	fim.checkImage((WRWindow) f.win);
	fim.waitForImageSize();
    }
}
