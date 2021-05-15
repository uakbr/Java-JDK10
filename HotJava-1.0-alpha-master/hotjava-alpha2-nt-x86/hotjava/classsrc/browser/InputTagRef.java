/*
 * @(#)InputTagRef.java	1.10 95/03/24 Jonathan Payne
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

package browser;

import browser.forms.FormItem;
import net.www.html.Tag;
import net.www.html.TagRef;

/**
 * An instance of class InputTagRef is created for each occurrence of
 * a &lt;input&gt; tag in an html form.  It serves as the definition of an
 * html INPUT item as it appears in an html document.  This is the
 * subclass of all &lt;input&gt; items which handles general book keeping,
 * and default initialization, the creation of the FormItem which
 * will handle the specifics of the type of form item this is.
 * @see FormItem
 * @see FormTagRef
 * @version 1.10, 24 Mar 1995
 * @author Jonathan Payne
 */

public class InputTagRef extends WRTagRef {
    FormItem	item;	    /** input item we represent */

    public InputTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    FormItem buildFormItem(WRFormatter f) {
	FormItem    i = null;
	FormTagRef  form;

	form = f.formContext();
	if (form != null) {
	    String	type = getAttribute("type");

	    if (type == null) {
		type = "text";
	    } else {
		type = type.toLowerCase();
	    }
	    try {

	        i = (FormItem) new("browser.forms." + type);
	    } catch (NoClassDefFoundException e) {
	        System.out.println("Warning: bad type for input tag = \""+type+"\" - using type=text instead.");
	        i = (FormItem) new("browser.forms.text");
	    }
	    i.initialize(f, form, this);
	    form.addInputItem(i);
	}
	return i;
    }

    public void addDisplayItem(WRFormatter f) {
	if (item != null) {
	    f.addDisplayItem(item.getDisplayItem(), true);
	    f.addCharacterSpacing(' ');
	}
    }

    public void apply(WRFormatter f) {
	if (item == null) {
	    item = buildFormItem(f);
	}
	item.prime(f);
	addDisplayItem(f);
    }
}
