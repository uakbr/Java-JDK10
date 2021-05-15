/*
 * @(#)WRFormatter.java	1.35 95/03/20 Jonathan Payne
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

import java.io.*;
import awt.*;
import java.util.*;
import net.www.html.TagRef;

/**
 * Class WRFormatter adds the functionality above and beyond that of
 * a basic formatter to layout html documents.  This includes
 * handling of html anchors, html list contexts, html forms, html
 * image alignment
 * @version 1.35, 20 Mar 1995
 * @author Jonathan Payne
 * @author James Gosling
 */
public
class WRFormatter extends Formatter {
    final boolean   debug = false;

    /** This is the document we're formatting. */
    Document	doc;

    /** Theoretically anchors should not nest, but just in case,
	the current anchor context is stored in this stack.  When
	display items are created, they check the current anchor
	stack and "do the right thing" if they are in an anchor
	context. */
    Stack	anchorStack = new Stack();

    /** This is the list stack.  <li> <dd> and <dt> all check their
	list "context" to figure out the proper margin setting, and
	look to the list context to generate the next bullet, etc. */
    Stack	listStack = new Stack();

    /** This is the current form we're defining.  There can
	only be one form at a time.  It is an error to have
	nested forms.  But considering how stupid mosaic is,
	I better allow for multiple forms JUST IN CASE.  So
	this is actually a stack. */
    Stack	formStack = new Stack();

    protected int getLength() {
	return doc.getText().length;
    }

    protected int charAt(int i) {
	return doc.getText()[i] & 0xFF;
    }

    /** Makes a hotjava text item. */
    protected TextDisplayItem makeTextItem(int pos0, int pos1) {
	TagRef	ac = anchorContext();

	return new WRTextItem(win, doc.getText(), pos0, pos1, ac);
    }

    /** Pushes a new anchor onto the anchor stack. */
    void pushAnchor(TagRef a) {
	anchorStack.push(a);
    }

    /** Pops an anchor from the anchor stack. */
    TagRef popAnchor() {
	return (TagRef) anchorStack.pop();
    }

    /**
     * Returns the current anchor context, or null if there is no
     * anchor.
     */
    TagRef anchorContext() {
	return anchorStack.size() > 0 ? (TagRef) anchorStack.peek() : null;
    }

    /** Pushes a new list onto the list stack. */
    void pushList(TagRef a) {
	listStack.push(a);
    }

    /**
     * Pops a new list onto the list stack.
     */
    void popList() {
	try {
	    listStack.pop();
	} catch (EmptyStackException e) {
	    System.out.print("List stack mess up\n");
	}
    }

    /**
     * Returns the current list context, or null if there is none.
     */
    WRListRef listContext() {
	return (listStack.size() > 0) ? (WRListRef) listStack.peek() : null;
    }

    /**
     * Pushes a new Form onto the form stack.  Technically there
     * should only ever be one form on the form stack at a time, but
     * knowing Mosaic anything is possible!
     */
    void pushForm(FormTagRef form) {
	formStack.push(form);
    }

    /** Pops a form from the form stack. */
    void popForm() {
	formStack.pop();
    }

    /**
     * Returns the current form context, or null if there is none.
     */
    FormTagRef formContext() {
	return (formStack.size() > 0) ? (FormTagRef) formStack.peek() : null;
    }

    /**
     * This adjusts a DisplayItem on the current line.  It's called
     * for each display item on a line, when the formatter finishes
     * the line, to go back and adjust items.  E.g., this handles
     * lining up baselines for all text that appeared on the line,
     * or, image alignment ala html and mosaic.
     */
    protected void adjustItem(DisplayItem di, int x) {
	if (di instanceof Alignable) {
	    Alignable	a = (Alignable) di;
	    int		align;

	    if ((align = a.getAlign()) != Alignable.A_NONE) {
		align = a.getAlign();
	    }
	    int	lineHeight = lineDescent + lineAscent;

	    switch (align) {
	    case Alignable.A_TOP:
		di.move(x, di.y);
		break;	    /* already there */

	    case Alignable.A_TEXTTOP:
		di.move(x, y + lineAscent - fontAscent);
		break;

	    case Alignable.A_ABSMIDDLE:
		di.move(x, y + (lineHeight - di.height) / 2);
		break;

	    case Alignable.A_MIDDLE:
		di.move(x, y + lineAscent - di.height / 2);
		break;

	    case Alignable.A_BASELINE:
	    case Alignable.A_BOTTOM:
		di.move(x, y + lineAscent - di.height);
		break;

	    case Alignable.A_ABSBOTTOM:
		di.move(x, y + lineHeight - di.height);
		break;
	    }
	} else {
	    super.adjustItem(di, x);
	}
    }

    /** This is to get around the motif bug which doesn't allow
	motif widgets to initially appear partially in the window
	(off the top).  Don't ask me why! */
    static final int	motifWidgetOffset = 0;
    final boolean	alignFirstLine = true;
    int			windowScrollY;

    protected void finishCurrentLine() {
	int start = lineStartIndex;
	int cnt = itemCount - start;
	int topY = y;
	int adjustedY = y + windowScrollY;
	int h = lineAscent + lineDescent;

	/* If we're aligning the first line so that it's not partially
	   visible off the top, and y WAS < 0 but now is > 0, then
	   adjust scrollY so that this line we're displaying now is not
	   negative. */
	if (alignFirstLine && adjustedY < motifWidgetOffset
	    && adjustedY + h > 0) {
	    win.setScrolling(0, -(topY - motifWidgetOffset));
	}

	super.finishCurrentLine();

	while (--cnt >= 0) {
	    win.updateChild(win.nthItem(start++), true);
	}
    }

    /**
     * Adds a display item to the window.  This takes into
     * consideration image alignment when adjusting the lineAscent
     * and lineDescent instance variables.  This doesn't worry about
     * positioning everything correctly at this point - that is
     * handled by adjustItem, which is called when the entire line
     * is layed out.
     */

    public boolean addDisplayItem(DisplayItem di, boolean checkForWrap) {
	int oldAscent = lineAscent;
	int oldDescent = lineDescent;

	if (!super.addDisplayItem(di, checkForWrap)) {
	    return false;
	}

	if (di instanceof Alignable) {
	    Alignable	wri = (Alignable)di;

	    /* First restore these.  The super class really doesn't
	       know what to do with anything other than bottom image
	       alignment, and that can screw us up, so we start with
	       a clean slate. */
	    lineAscent = oldAscent;
	    lineDescent = oldDescent;

	    int	align = wri.getAlign();
	    if (align == Alignable.A_NONE) {
		align = Alignable.A_BOTTOM;
	    }

	    /* Now check to see if this image and the current
	       image alignment changes the line ascent or descent.

	       NOTE: If no text has occurred on the line, and we
	       come across a TEXTTOP or TOP image, lineDescent is
	       adjusted to the height of the image.  This is because
	       lineAscent and fontAscent are 0 at the time. I don't
	       know how to fix this yet. */

	    switch (align) {
	    case Alignable.A_TEXTTOP:
		lineDescent = Math.max(lineDescent, di.height - fontAscent);
		break;

	    case Alignable.A_TOP:
		lineDescent = Math.max(lineDescent, di.height - lineAscent);
		break;

	    case Alignable.A_ABSMIDDLE:
		{
		    int	lineHeight = lineAscent + lineDescent;
		}
		/* falls into */
		    

	    case Alignable.A_MIDDLE:
		{
		    int	halfHeight = di.height / 2;

		    lineAscent = Math.max(lineAscent, halfHeight);
		    lineDescent = Math.max(lineDescent, halfHeight);
		    break;
		}

	    case Alignable.A_BASELINE:
	    case Alignable.A_BOTTOM:
		lineAscent = Math.max(lineAscent, di.height);
		break;

	    case Alignable.A_ABSBOTTOM:
		lineAscent = Math.max(lineAscent, di.height - lineDescent);
		break;
	    }
	}
	return true;
    }	    

    protected Vector	tagList;
    protected int	tagIndex;

    protected int processStyleRefs(int pos) {
	WRTagRef    ref = null;
	int		tagLimit = tagList.size();

	while (tagIndex < tagLimit) {
	    ref = (WRTagRef) tagList.elementAt(tagIndex);
	    if (ref.pos > pos) {
		break;
	    }
	    if (debug) {
		System.out.println("Processing ref #" + tagIndex + "[pos = " + pos + "] = " + ref);
	    }
	    ref.apply(this);
	    tagIndex += 1;
	}
	if (tagIndex < tagLimit) {
	    return ref.pos;
	} else {
	    return doc.getText().length;
	}
    }

    protected void reset() {
	super.reset();
	y = 20;
	windowScrollY = win.getScrollY();
    }

    public void layout() {
	Document    doc = ((WRWindow) win).document();

	xlimit = win.width;
	if (xlimit < 20) {
	    return;
	}
	anchorStack.setSize(0);
	listStack.setSize(0);

	tagList = doc.getTags();
	tagIndex = 0;

	/* now, walk through all the tag refs, and start fetching images */
	int i = 0;
	int cnt = tagList.size();
	while (--cnt >= 0) {
	    TagRef  ref = (TagRef) tagList.elementAt(i++);

	    if (ref instanceof ImgTagRef) {
		((ImgTagRef) ref).prime((WRWindow) win);
	    }
	}

	super.layout();
    }

    public WRFormatter(WRWindow m, Document d) {
	super(m);
	doc = d;
    }
}
