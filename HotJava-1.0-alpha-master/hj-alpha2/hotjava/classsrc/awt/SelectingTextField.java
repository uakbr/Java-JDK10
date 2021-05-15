/*
 * @(#)SelectingTextField.java	1.3 95/01/31 95/01/13 Herb Jellinek
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

package awt;

/**
 * SelectingTextField is a GUI element that allows a single line of text
 * input, as with TextField.  However, the selected() method is defined to
 * call its okCallback method (defined by interface DialogHandler), or
 * the okCallback method of its handler object.
 *
 * @version 1.3 31 Jan 1995
 * @author Herb Jellinek
 */
class SelectingTextField extends TextField implements DialogHandler {
    public DialogHandler handler;

    Dialog myDialog;

    public SelectingTextField(String initValue,
			      String pName,
			      Window p,
			      Dialog dialog,
			      DialogHandler hand) {
	super(initValue, pName, p, true);
	myDialog = dialog;
	handler = hand;
    }

    /** User said 'ok' to current text. */
    public void okCallback(Dialog d) {
	System.out.println("SelectingTextField: OK");
	if (handler == null) {
	    d.hide();
	} else {
	    handler.okCallback(d);
	}
    }

    /** User said to cancel current text. */
    public void cancelCallback(Dialog d) {
	if (handler == null) {
	    d.hide();
	} else {
	    handler.cancelCallback(d);
	}
    }

    /** User's asking for help. */
    public void helpCallback(Dialog d) {
	if (handler == null) {
	    System.out.println("No help available.");
	} else {
	    handler.helpCallback(d);
	}
    }

    public void selected() {
	okCallback(myDialog);
    }

}

    
