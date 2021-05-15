/*
 * @(#)textarea.java	1.4 95/03/14 Jonathan Payne
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

import awt.DisplayItem;
import awt.DisplayItemWindow;
import awt.NativeDisplayItem;
import awt.TextArea;
import awt.Component;
import awt.Font;
import browser.WRFormatter;

/**
 * An instance of class text is created for each occurrence of a
 * text field in an html form.
 * @version 1.5, 12 Dec 1994
 * @author Jonathan Payne
 */

public class textarea extends FormItem {
    public DisplayItem buildDisplayItem(WRFormatter f) {
	NativeDisplayItem   ndi = new FormDisplayItem();
	Font		    font = f.win.wServer.fonts.getFont("Courier:12");
	int		    columns, rows;
	Component	    c;

	columns = getIntegerAttribute("cols", 50);
	rows = getIntegerAttribute("rows", 5);

	ndi.setComponent(c = new TextArea(f.win, null, font, columns, rows));

	ndi.resize(c.width, c.height);

	return ndi;
    }

    public void setInitialValue(String value) {
	defaultValue = value;
	acceptStringValue(value);
    }

    public void acceptStringValue(String value) {
	TextArea   ta =
	    (TextArea) ((NativeDisplayItem) displayItem).getComponent();

	ta.setText(value);
    }

    public String getFormValue() {
	TextArea   ta =
	    (TextArea) ((NativeDisplayItem) displayItem).getComponent();

	return ta.getText();
    }
}
