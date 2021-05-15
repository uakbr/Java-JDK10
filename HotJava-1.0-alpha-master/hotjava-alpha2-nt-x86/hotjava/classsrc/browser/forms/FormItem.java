/*
 * @(#)FormItem.java	1.12 95/03/19 Jonathan Payne
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

import browser.*;
import awt.DisplayItem;
import awt.NativeDisplayItem;
import awt.Component;

/**
 * Class FormItem is the superclass of all items that can appear in a
 * html form.  FormItem is subclassed by the various types of input
 * items that can appear in forms.  The responsibility of the
 * subclasses is to return a String value appropriate for that item,
 * and for creating the native display item that will actually
 * appear in the document.
 *
 * @see FormTagRef
 * @see InputTagRef
 * @version 1.12, 19 Mar 1995
 * @author Jonathan Payne
 */

public class FormItem {
    static int	flagBits[] = new int[256 / 32];
    static {
	int i;

	for (i = 0; i < flagBits.length; i++) {
	    flagBits[i] = -1;
	}
	for (i = 'a'; i <= 'z'; i++) {
	    clrBit(i);
	}
	for (i = 'A'; i <= 'Z'; i++) {
	    clrBit(i);
	}
	for (i = '0'; i <= '9'; i++) {
	    clrBit(i);
	}
	clrBit('_');
	clrBit(' ');
    }

    static void clrBit(int i) {
	flagBits[i >> 5] &= ~(1 << (i & 31));
    }

    static boolean charNeedsEncoding(int c) {
	return (flagBits[c >> 5] & (1 << (c & 31))) != 0;
    }

    /** This is the <INPUT> tag ref that causes this FormItem
	to be created. */
    protected InputTagRef	owner;

    /** This is the <FORM> tag ref in which this item appears. */
    protected FormTagRef	form;

    /** The name of this input item (required). */
    protected String	name;

    /** The default value of the input item (optional). */
    protected String	defaultValue;

    /** The display item associated with this FormItem. */
    protected DisplayItem	displayItem = null;

    /** Null constructor because we're called from new("<string>"),
	for which only the null constructor may be called. */
    public FormItem() {}

    public void initialize(WRFormatter f, FormTagRef form, InputTagRef thisItem) {
	this.form = form;
	owner = thisItem;
	name = thisItem.getAttribute("name");
	defaultValue = thisItem.getAttribute("value");
	if (displayItem == null) {
	    displayItem = buildDisplayItem(f);
	}
    }

    /** Walks through the value string, escaping any special
	characters that appear in the string. */
    final String processString(String value) {
	StringBuffer	buf = new StringBuffer(value.length() + 10);
	int		cnt = value.length();

	for (int i = 0; --cnt >= 0; i++) {
	    int	c;

	    c = value.charAt(i);
	    if (charNeedsEncoding(c)) {
		buf.appendChar('%');
		buf.appendChar(Character.forDigit(c >> 4, 16));
		buf.appendChar(Character.forDigit(c & 0xF, 16));
	    } else {
		if (c == ' ') {
		    c = '+';
		}
		buf.appendChar(c);
	    }
	}
	return buf.toString();
    }

    protected int getIntegerAttribute(String name, int Default) {
	int value;

	try {
	    value = Integer.parseInt(owner.getAttribute(name));
	} catch (NumberFormatException e) {
	    value = Default;
	} catch (NullPointerException e) {
	    value = Default;
	}
	return value;
    }

    /**
     * Gets the form string which is appropriate for including in
     * the resulting form URL.  It's checked characters that need
     * special encoding and processed appropriately.  Form items
     * with more than one value will have to override this method to
     * generate the right string.  Form items with a single value
     * can pretty much leave this alone and just override
     * getFormValue().
     */
    public String getFormString() {
	String value = getFormValue();

	if (value != null) {
	    value = name + "=" + processString(value);
	}
	return value;
    }

    /** Returns the display item associated with the form item. */
    public DisplayItem getDisplayItem() {
	return displayItem;
    }
	
    /** Returns the FormTagRef associated with this FormItem. */
    public FormTagRef getFormTagRef() {
	return form;
    }

    /** Returns the InputTagRef associated with this FormItem. */
    public InputTagRef getTagRef() {
	return owner;
    }

    /**
     * Returns the name of this form item, or null if one wasn't
     * specified.
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the component associated with the NativeDisplayItem
     * that was created for this FormItem.
     * @exception ClassCastException when the display item created
     * for this FormItem is not a NativeDisplayItem
     */
    public Component getComponent() {
	return ((NativeDisplayItem) displayItem).getComponent();
    }

    /** Resets the this FormItem to its default/initial value. */
    public void reset() {
	acceptStringValue(defaultValue);
    }

    /** This is called when this particular form item is "activated".
	E.g., we're a button and we've been pressed. */
    public void execute() {}

    public abstract DisplayItem buildDisplayItem(WRFormatter f);
    public abstract void acceptStringValue(String newValue);

    /**
     * This is called to get the item to load any resources it needs for
     * displaying and to verify its size (used only by image input items
     * currently).
     */
    public void prime(WRFormatter f) {
    }

    /**
     * Return the value of this form Item, which by default is the
     * defaultValue.
     */
    public String getFormValue() {
	return defaultValue;
    }
}
