/*
 * @(#)text.java	1.12 95/04/10 Jonathan Payne
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
import awt.TextField;
import awt.Font;
import awt.Component;
import browser.WRFormatter;

/**
 * An instance of class text is created for each occurrence of a
 * text field in an html form.
 * @version 1.12, 10 Apr 1995
 * @author Jonathan Payne
 */

public class text extends FormItem {
    private boolean isIndex() {
	return name.toLowerCase().equals("isindex");
    }

    public DisplayItem buildDisplayItem(WRFormatter f) {
	NativeDisplayItem   ndi = new FormDisplayItem();

	ndi.setComponent(new FormTextField(defaultValue, f.win, this));

	int	size;
	Font	font = f.getFont();
	int	width = -1;

	/* REMIND: Stick in code to use a fixed width font in
	   the text field, so character counting is accurate. */

	size = getIntegerAttribute("size", 20);
	width = f.win.getFontMetrics(font).charWidth('m') * size;
	Component   c = ndi.getComponent();
	c.resize(width, c.height);
	ndi.resize(c.width, c.height);

	return ndi;
    }

    public String getFormString() {
	if (isIndex()) {
	    return processString(getFormValue());
	} else {
	    return super.getFormString();
	}
    }

    public void acceptStringValue(String value) {
	TextField   tf =
	    (TextField) ((NativeDisplayItem) displayItem).getComponent();

	tf.setText(value);
    }

    public String getFormValue() {
	TextField   tf =
	    (TextField) ((NativeDisplayItem) displayItem).getComponent();

	return tf.getText();
    }

    public void execute() {
	if (isIndex()) {
	    form.submit();
	}
    }
}
