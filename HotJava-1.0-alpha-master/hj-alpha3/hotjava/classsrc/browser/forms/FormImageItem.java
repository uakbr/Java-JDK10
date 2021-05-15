/*
 * @(#)FormImageItem.java	1.5 95/03/20 Jonathan Payne
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

import awt.Event;
import browser.WRImageItem;
import browser.WRWindow;
import browser.hotjava;

/**
 * Class FormImageItem is created for each image that appears in an
 * html form.  It associates a FormItem with itself, and calls into
 * the FormItem when the button is pressed.
 * @see FormItem
 * @version 1.5, 20 Mar 1995
 * @author Jonathan Payne
 */
public class FormImageItem extends WRImageItem {
    image   imageFormItem;

    public FormImageItem(WRWindow wr, image i) {
	super(wr, i.getTagRef(), null);
	imageFormItem = i;
	setColor(hotjava.anchorColor);
    }

    protected void initializeBorderThickness() {
	borderThickness = 2;
    }

    protected void executeClick(Event e) {
	imageFormItem.Execute(e);
    }
}
