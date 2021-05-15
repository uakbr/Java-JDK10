/*
 * @(#)reset.java	1.6 95/03/14 Jonathan Payne
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

import browser.FormTagRef;
import awt.DisplayItem;
import awt.DisplayItemWindow;
import awt.NativeDisplayItem;
import awt.TextField;
import awt.Font;
import browser.WRFormatter;

/**
 * An instance of class reset is created for each occurrence of a
 * submit button in an html form.
 * @version 1.6, 14 Mar 1995
 * @author Jonathan Payne
 */
public class reset extends button {
    public DisplayItem buildDisplayItem(WRFormatter f) {
	if (defaultValue == null) {
	    defaultValue = "Reset";
	}
	return super.buildDisplayItem(f);
    }

    public void execute() {
	FormTagRef  ftr = getFormTagRef();

	activated = true;
	ftr.reset();
    }
}
