/*
 * @(#)hidden.java	1.5 95/03/14 Jonathan Payne
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
import browser.WRFormatter;

/**
 * An instance of class hiddne is created for each occurrence of a
 * <input type=hidden> in an html form.
 * @version 1.5, 14 Mar 1995
 * @author Jonathan Payne
 */
public class hidden extends FormItem {
    public DisplayItem buildDisplayItem(WRFormatter f) {
	return new DisplayItem();
    }
    public void acceptStringValue(String newValue) {}
}
