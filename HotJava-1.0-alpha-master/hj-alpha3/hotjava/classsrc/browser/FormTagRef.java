/*
 * @(#)FormTagRef.java	1.15 95/04/07 Jonathan Payne
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

import java.util.Hashtable;
import awt.RadioGroup;
import java.util.Vector;
import browser.forms.FormItem;
import net.www.html.Tag;
import net.www.html.TagRef;
import net.www.html.URL;
import browser.forms.submit;

/**
 * An instance of class FormTagRef is created for each occurrence of
 * a &lt;form&gt; tag in an html document.  It serves as the definition of
 * an html FORM object as it appears in an html document.
 * @version 1.15, 07 Apr 1995
 * @author Jonathan Payne
 */

public class FormTagRef extends WRTagRef {
    /**
     * The WRWindow this tag ref appears in.  This is needed to
     * perform the PushURL that is done when the form is submitted.
     */
    WRWindow	win;

    /** The vector of &lt;input&gt; items that appear in this form. */
    Vector	inputItems;

    /** The url of the document this form appears in. */
    URL		url;

    /**
     * The different radio groups (by name) that appear in this hash
     * table.
     */
    Hashtable	radioGroups;

    /**
     * The current textarea or select tag we're processing.
     */
    TagRef	currentInput;

    public FormTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void apply(WRFormatter f) {
	win = (WRWindow) f.win;
	url = win.document().url();
	if (isEnd) {
	    f.popForm();
	} else {
	    f.pushForm(this);
	}
    }

    /**
     * Adds an input item to this form.  This is called by input
     * items which are created as a result of &lt;input&gt; tags that
     * appear in html documents.
     */
    public void addInputItem(FormItem item) {
	if (inputItems == null) {
	    inputItems = new Vector();
	}
	inputItems.addElement(item);
    }

    /**
     * Returns the radio group associated with the specified name.
     * If there is no RadioGroup by that name, one is created.
     */
    public RadioGroup getRadioGroup(String name) {
	if (radioGroups == null) {
	    radioGroups = new Hashtable();
	}

	RadioGroup  group = (RadioGroup) radioGroups.get(name);

	if (group == null) {
	    group = new RadioGroup();
	    radioGroups.put(name, group);
	}

	return group;
    }

    /**
     * Set the current input item tag.
     * @exception Exception if there's already a select tag
     */
    public void setInputItem(TagRef r) {
	if (currentInput != null && r != null) {
	    throw new Exception("nested " + r.tag + " tags not allowed");
	}
	currentInput = r;
    }

    /**
     * Get the current select tag or return null if we're currently
     * not processing one.
     */
    public TagRef getInputItem() {
	return currentInput;
    }

    /**
     * Submits this form.  It walks through each input item asking
     * it for its FormString.  Some form items may choose not to
     * return a value.
     */
    public void submit() {
	int cnt = inputItems.size();
	int i = 0;
	String	query;
	URL	submitUrl;
	String	action = getAttribute("action");
	boolean posting;

	try {
	    posting = getAttribute("method").toLowerCase().equals("post");
	} catch (NullPointerException e) {
	    posting = false;
	}

	if (action != null) {
	    submitUrl = new URL(url, action);
	} else {
	    submitUrl = url;
	}

	query = "";

	try {
	    boolean doneOne = false;

	    while (--cnt >= 0) {
		FormItem	fi = (FormItem) inputItems.elementAt(i++);

		/* items without names don't get reported */
		if (fi.getName() == null) {
		    continue;
		}
		String	thisResult = fi.getFormString();

		if (thisResult != null) {
		    if (doneOne) {
			query += "&";
		    }
		    query += thisResult;
		    doneOne = true;
		}
	    }
	    if (posting) {
		submitUrl = new URL(submitUrl, query, url);
	    } else {
		/* REMIND: This belongs in class URL. */
		String	currentUrl = submitUrl.toExternalForm();
		int	questionIndex;

		if ((questionIndex = currentUrl.lastIndexOf('?')) != -1) {
		    currentUrl = currentUrl.substring(0, questionIndex);
		}
		submitUrl = new URL(null, currentUrl + "?" + query);
	    }
	    win.pushURL(submitUrl);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /** Resets all the form input items to their default value. */
    public void reset() {
	int cnt = inputItems.size();
	int i = 0;

	while (--cnt >= 0) {
	    FormItem	fi = (FormItem) inputItems.elementAt(i++);

	    fi.reset();
	}
    }
}
