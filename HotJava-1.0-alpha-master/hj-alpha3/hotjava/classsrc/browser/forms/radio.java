/*
 * @(#)radio.java	1.6 95/03/14 Jonathan Payne
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
import awt.Toggle;
import awt.Component;
import awt.DisplayItemWindow;
import awt.NativeDisplayItem;
import awt.TextField;
import awt.Font;
import awt.RadioGroup;
import browser.WRFormatter;

/**
 * An instance of class radio is created for each occurrence of a
 * radio button in an html form.
 * @version 1.6, 14 Mar 1995
 * @author Jonathan Payne
 */

public class radio extends FormItem {
    boolean checked = false;

    public DisplayItem buildDisplayItem(WRFormatter f) {
	NativeDisplayItem   ndi = new FormDisplayItem();
	RadioGroup	    group;

	checked = owner.getAttribute("checked") != null;
	group = form.getRadioGroup(name);

	Component   c = new Toggle("", null, f.win, group, checked);

	ndi.setComponent(c);
	ndi.reshape(ndi.x, ndi.y, c.width, c.height);

	return ndi;
    }

    public void acceptStringValue(String newValue) {}
	    
    public void reset() {
	setValue(checked);
    }

    public String getFormValue() {
	if (!getValue()) {
	    return null;
	}
	return (defaultValue == null) ? "on" : defaultValue;
    }

    void setValue(boolean value) {
	Toggle	t = (Toggle) getComponent();

	t.setState(value);
    }

    boolean getValue() {
	Toggle	t = (Toggle) getComponent();

	return t.getState();
    }
}
