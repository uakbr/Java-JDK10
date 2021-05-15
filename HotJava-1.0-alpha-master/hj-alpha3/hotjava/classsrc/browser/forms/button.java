/*
 * @(#)button.java	1.8 95/03/14 Jonathan Payne
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

import awt.Component;
import awt.DisplayItem;
import awt.DisplayItemWindow;
import awt.NativeDisplayItem;
import awt.Font;
import awt.Button;
import browser.WRFormatter;

/**
 * An instance of class button is created for each occurrence of a
 * push button in an html form.
 * @version 1.8, 14 Mar 1995
 * @author Jonathan Payne
 */

public class button extends FormItem {
    protected boolean	activated = false;

    public DisplayItem buildDisplayItem(WRFormatter f) {
	NativeDisplayItem   ndi = new FormDisplayItem();
	Component	    c = new FormButton(defaultValue, f.win, this);

	ndi.setComponent(c);
	ndi.reshape(ndi.x, ndi.y, c.width, c.height);

	return ndi;
    }

    public void acceptStringValue(String value) {
	defaultValue = value;
    }

    /**
     * Return the value of this form Item.  In the case of buttons,
     * which are either submit or reset, a value is returned if the
     * button is the one activated, and it has a name.  Otherwise
     * null is returned for this item.  We're not even called unless
     * we have a name, so we just check for activated.
     */
    public String getFormValue() {
	if (activated) {
	    activated = false;
	    return defaultValue;
	}
	return null;
    }
}
