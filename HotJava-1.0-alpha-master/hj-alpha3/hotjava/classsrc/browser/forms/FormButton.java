/*
 * @(#)FormButton.java	1.5 95/03/14 Jonathan Payne
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
import awt.Font;
import awt.Button;
import awt.Window;
import browser.WRFormatter;

/**
 * Class FormButton is created for each button that appears in an
 * html form.  It associates a FormItem with itself, and calls into
 * the FormItem when the button is pressed.
 * @see FormItem
 * @version 1.5, 14 Mar 1995
 * @author Jonathan Payne
 */

public class FormButton extends Button {
    FormItem	formItem;

    public FormButton(String value, Window win, FormItem formItem) {
	super(value, null, win);
	this.formItem = formItem;
    }

    public void selected(Component c, int pos) {
	formItem.execute();
    }
}
