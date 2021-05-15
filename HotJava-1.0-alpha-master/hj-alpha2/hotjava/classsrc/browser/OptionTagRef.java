/*
 * @(#)OptionTagRef.java	1.5 95/03/14 Jonathan Payne
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

import net.www.html.Tag;
import net.www.html.TagRef;

/**
 * Class OptionTagRef is created for <option> tags that appear in html
 * document.
 * @version 1.6, 12 Dec 1994
 * @author Jonathan Payne
 */

public class OptionTagRef extends WRTagRef {
    public OptionTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void apply(WRFormatter f) {
	FormTagRef  ref = f.formContext();

	if (ref != null) {
	    SelectTagRef    select = (SelectTagRef) ref.getInputItem();

	    if (select != null) {
		select.newOption(f.doc.getText(), this);
	    }
	}
    }
}
