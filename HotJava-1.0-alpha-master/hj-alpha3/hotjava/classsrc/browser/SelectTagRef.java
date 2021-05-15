/*
 * @(#)SelectTagRef.java	1.8 95/03/17 Jonathan Payne
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
import browser.forms.select;
import awt.Component;
import awt.NativeDisplayItem;
import net.www.html.Tag;
import net.www.html.TagRef;

/** A SelectTagRef is just like an InputTagRef.  We just stick in
    the "type" field in the TagRef for them. */

class SelectTagRef extends InputTagRef {
    TagRef  lastOptionRef = null;

    public SelectTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void newOption(byte text[], TagRef ref) {
	if (lastOptionRef != null) {
	    String  option = new String(text, 0, lastOptionRef.pos, ref.pos - lastOptionRef.pos);

	    ((select) item).addOption(option,
				      lastOptionRef.getAttribute("value"),
				      lastOptionRef.getAttribute("selected") != null);
	}
	lastOptionRef = ref;
    }

    public void finish() {
	((select) item).finish();
    }

    /* We override this here so that we can make sure we have the
       actual size. */
    public void addDisplayItem(WRFormatter f) {
	if (item != null) {
	    NativeDisplayItem	ndi = (NativeDisplayItem) item.getDisplayItem();

	    if (ndi != null) {
		Component   c = ndi.getComponent();

		ndi.resize(c.width, c.height);
	    }
	    super.addDisplayItem(f);
	}
    }

    public void apply(WRFormatter f) {
	try {
	    FormTagRef	    form = f.formContext();
	    SelectTagRef    select = (SelectTagRef) form.getInputItem();

	    if (!isEnd) {
		if (item == null) {
		    item = buildFormItem(f);
		}
		form.setInputItem(this);
		lastOptionRef = null;
		f.stopRendering();
	    } else {
		select.newOption(f.doc.getText(), this);
		select.finish();
		form.setInputItem(null);
		if (select != null) {
		    select.addDisplayItem(f);
		}
		f.startRendering();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Error: " + e + " while processing " + this.toExternalForm());
	}
    }

    /*
     * Unlike input tags, select tags can only have select items
     * inside of them.
     */
    FormItem buildFormItem(WRFormatter f) {
	FormItem    i = null;
	FormTagRef  form;

	form = f.formContext();
	if (form != null) {
	    i = (FormItem) new("browser.forms.select");
	    i.initialize(f, form, this);
	    form.addInputItem(i);
	}
	return i;
    }




}
