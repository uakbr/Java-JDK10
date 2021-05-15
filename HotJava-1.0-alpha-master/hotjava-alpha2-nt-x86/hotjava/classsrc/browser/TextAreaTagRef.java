/*
 * @(#)TextAreaTagRef.java	1.5 95/03/14 Jonathan Payne
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
import browser.forms.textarea;
import net.www.html.Tag;
import net.www.html.TagRef;

/** A TextAreaTagRef is just like an InputTagRef.  We just stick in
    the "type" field in the TagRef for them. */

class TextAreaTagRef extends InputTagRef {
    boolean textInitialized = false;

    public TextAreaTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);

	addAttribute("type", "textarea");
    }

    void initializeText(byte text[], TagRef ref) {
	String  value = new String(text, 0, pos, ref.pos - pos);

	((textarea) item).setInitialValue(value);
    }

    public void apply(WRFormatter f) {
	try {
	    FormTagRef	    form = f.formContext();
	    TextAreaTagRef  text = (TextAreaTagRef) form.getInputItem();

	    if (!isEnd) {
		super.apply(f);
		form.setInputItem(this);
		f.stopRendering();
	    } else {
		if (!textInitialized) {
		    text.initializeText(f.doc.getText(), this);
		    textInitialized = true;
		}
		form.setInputItem(null);
		f.startRendering();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Error: " + e + " while processing " + this.toExternalForm());
	}
    }
}
