/*
 * @(#)ImgTagRef.java	1.15 95/03/19 Jonathan Payne
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

import awt.DisplayItem;
import net.www.html.Tag;
import net.www.html.TagRef;

/**
 * An instance of ImgTagRef is created for each instance of an <img>
 * tag in an html document.
 * @version 1.15, 19 Mar 1995
 * @author Jonathan Payne
 */

public class ImgTagRef extends DisplayItemTagRef {
    public ImgTagRef(Tag t, int pos, boolean isEnd) {
	super(t, pos, isEnd);
    }

    public void prime(WRWindow w) {
	if (!WRWindow.delayImageLoading) {
	    WRImageItem.kickImage(w, this);
	}
    }

    public void buildDisplayItem(WRFormatter f) {
	TagRef	anchor = f.anchorContext();
	String	href = null;

	if (anchor != null) {
	    href = anchor.getAttribute("href");
	}
	di = new WRImageItem((WRWindow) f.win, this, href);
    }

    public void apply(WRFormatter f) {
	WRImageItem wim = (WRImageItem) di;

	if (wim == null) {
	    buildDisplayItem(f);
	    wim = (WRImageItem) di;
	} else {
	    wim.checkImage(f.win);
	}

	wim.waitForImageSize();
	super.apply(f);
    }
}
