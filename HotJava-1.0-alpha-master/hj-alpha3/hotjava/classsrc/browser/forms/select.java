/*
 * @(#)select.java	1.7 95/03/14 Jonathan Payne
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
import awt.Component;
import awt.DisplayItemWindow;
import awt.NativeDisplayItem;
import awt.OptionMenu;
import awt.List;
import browser.WRFormatter;
import java.util.Hashtable;

/**
 * An instance of class option is created for each occurrence of a
 * <select size=1> in a form.
 *
 * @version 1.3, 12 Dec 1994
 * @author Jonathan Payne
 */

public class select extends FormItem {
    boolean useOptionMenu;
    boolean selected;
    boolean finished = false;
    Hashtable	optionValues;

    public DisplayItem buildDisplayItem(WRFormatter f) {
	NativeDisplayItem   ndi = new FormDisplayItem();
	String		    sizeAttribute;
	int		    size = 1;
	boolean		    multiple;

	size = getIntegerAttribute("size", 1);
	multiple = owner.getAttribute("multiple") != null;
	    
	useOptionMenu = !(multiple || size >= 2);

	Component c;

	if (useOptionMenu) {
	    c = new OptionMenu(f.win, "", null);
	} else {
	    c = new List(f.win, null, null, size, multiple);
	}
	ndi.setComponent(c);
	ndi.reshape(ndi.x, ndi.y, c.width, c.height);

	if (optionValues != null) {
	    optionValues = null;
	}

	return ndi;
    }

    public void addOption(String option, String value, boolean selected) {
	/* This item has already been built once, and now we're building
	   it again because we're relayingout the document.  But we want
	   to preserve the current settings.  This feels slightly bogus. */
	if (finished) {
	    return;
	}

	int limit = option.length();
	int i0 = 0;
	int c;

	while (i0 < limit) {
	    if (!((c = option.charAt(i0)) == ' ' || c == '\t' || c == '\n'))
		break;
	    i0 += 1;
	}
	int i1 = limit;
	while (i1 > i0) {
	    if (!((c = option.charAt(i1 - 1)) == ' ' || c == '\t' || c == '\n'))
		break;
	    i1 -= 1;
	}
	option = option.substring(i0, i1);

	/* If a value was associated with this option, store that
	   now.  It's used instead of the name of the option, when
	   the value of this select is requested. */
	if (value != null) {
	    if (optionValues == null) {
		optionValues = new Hashtable();
	    }
	    optionValues.put(option, value);
	}

	int count;

	if (useOptionMenu) {
	    OptionMenu  om = (OptionMenu) getComponent();

	    om.addItem(option);
	    count = om.nItems();
	} else {
	    List    list = (List) getComponent();

	    list.addItem(option);
	    count = list.nItems();
	}
	if (selected) {
	    select(count - 1);
	}
    }

    public void finish() {
	if (!finished) {
	    if (!selected) {
		select(0);
	    }
	    finished = true;
	}
    }

    void select(int item) {
	if (useOptionMenu) {
	    OptionMenu  om = (OptionMenu) getComponent();

	    om.select(item);
	} else {
	    List    list = (List) getComponent();

	    list.select(item);
	}
	selected = true;
    }	    

    public void acceptStringValue(String newValue) {}
	    
    public void reset() {
    }

    /* If a value=foo was specified, return the value; otherwise,
       return the actual item displayed in the list/option menu. */
    private String optionSpecifiedValue(String option) {
	if (optionValues != null) {
	    String  value = (String) optionValues.get(option);

	    if (value != null) {
		return value;
	    }
	}
	return option;
    }

    final String getOptionMenuValue() {
	OptionMenu  om = (OptionMenu) getComponent();

	return optionSpecifiedValue(om.selectedItem());
    }

    private int	offset;

    final String getListValue() {
	List    list = (List) getComponent();

	int	cnt = list.nItems() - offset;

	while (--cnt >= 0) {
	    if (list.isSelected(offset++)) {
		return optionSpecifiedValue(list.itemAt(offset - 1));
	    }
	}
	return null;
    }

    public String getFormString() {
	if (useOptionMenu) {
	    return name + "=" + processString(getOptionMenuValue());
	} else {
	    String  value;
	    String  result = "";
	    boolean doneOne = false;

	    offset = 0;

	    while ((value = getListValue()) != null) {
		if (doneOne) {
		    result += "&";
		}
		result += name + "=" + processString(value);
		doneOne = true;
	    }
	    return result;
	}
    }

    public String getFormValue() {
	throw new Exception("Internal error: select form item");
    }
}
